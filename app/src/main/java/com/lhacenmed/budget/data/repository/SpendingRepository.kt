package com.lhacenmed.budget.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.lhacenmed.budget.data.local.AppDatabase
import com.lhacenmed.budget.data.local.CachedContribution
import com.lhacenmed.budget.data.local.PendingSpendingItem
import com.lhacenmed.budget.data.local.toCached
import com.lhacenmed.budget.data.model.BudgetContribution
import com.lhacenmed.budget.data.model.SpendingItem
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@RequiresApi(Build.VERSION_CODES.O)
@Singleton
class SpendingRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val db: AppDatabase
) {
    private val pendingDao      get() = db.pendingItemDao()
    private val spendingCache   get() = db.cachedSpendingDao()
    private val contributionCache get() = db.cachedContributionDao()

    // ── Spending ──────────────────────────────────────────────────────────────

    /**
     * Network-first: fetch from Supabase, write to cache, return result.
     * Offline fallback: return cached data transparently.
     */
    suspend fun getAllSpending(): List<SpendingItem> =
        runCatching {
            val items: List<SpendingItem> = supabase.from("spending_items").select().decodeList()
            spendingCache.upsertAll(items.map { it.toCached() })
            items
        }.getOrElse {
            spendingCache.getAll().map { it.toSpendingItem() }
        }

    /**
     * Derives days from the cache (already populated by getAllSpending).
     * No extra network call needed — getDays() and getAllSpending() always
     * run together in the ViewModel's parallel fetch.
     */
    suspend fun getDays(): List<String> =
        runCatching {
            // Try to get fresh data from Supabase via the cache already written
            // by the concurrent getAllSpending() call. If that hasn't run yet,
            // fall through to the direct Supabase query.
            supabase.from("spending_items")
                .select(io.github.jan.supabase.postgrest.query.Columns.raw("date"))
                .decodeList<Map<String, String>>()
                .mapNotNull { it["date"] }
                .distinct()
                .sortedDescending()
        }.getOrElse {
            spendingCache.getDates()
        }

    /** Always cache-backed — fast, no network hit even when online. */
    suspend fun getItemsForDay(date: String): List<SpendingItem> =
        spendingCache.getByDate(date).map { it.toSpendingItem() }

    /**
     * Offline-first write:
     * 1. Save to Room pending queue immediately.
     * 2. Try Supabase — on success, remove from pending and cache the returned item.
     * 3. On failure — item stays in queue; syncPending() retries on reconnect.
     */
    suspend fun addItem(item: SpendingItem) {
        val localId = pendingDao.insert(item.toPending()).toInt()
        runCatching {
            supabase.from("spending_items").insert(item) {
                select()
            }.decodeSingle<SpendingItem>().also { saved ->
                spendingCache.upsert(saved.toCached())
                pendingDao.deleteById(localId)
            }
        }
        // Failure is silent — item stays in pending queue for syncPending()
    }

    /** Drains the local queue to Supabase when connectivity is restored. */
    suspend fun syncPending() {
        pendingDao.getAll().forEach { pending ->
            runCatching {
                supabase.from("spending_items").insert(pending.toSpendingItem()) {
                    select()
                }.decodeSingle<SpendingItem>().also { saved ->
                    spendingCache.upsert(saved.toCached())
                    pendingDao.deleteById(pending.localId)
                }
            }
        }
    }

    suspend fun pendingCount(): Int = pendingDao.count()

    suspend fun deleteItem(id: String) {
        // Remove from cache immediately for instant UI update
        spendingCache.deleteById(id)
        runCatching {
            supabase.from("spending_items").delete { filter { eq("id", id) } }
        }
        // Cache deletion already happened — UI is consistent even if network fails
    }

    // ── Budget contributions ──────────────────────────────────────────────────

    suspend fun getContributions(): List<BudgetContribution> =
        runCatching {
            val contributions: List<BudgetContribution> =
                supabase.from("budget_contributions")
                    .select { order("created_at", Order.DESCENDING) }
                    .decodeList()
            contributionCache.upsertAll(contributions.map { it.toCached() })
            contributions
        }.getOrElse {
            contributionCache.getAll().map { it.toContribution() }
        }

    suspend fun addContribution(contribution: BudgetContribution) {
        runCatching {
            supabase.from("budget_contributions").insert(contribution) {
                select()
            }.decodeSingle<BudgetContribution>().also { saved ->
                contributionCache.upsert(
                    CachedContribution(
                        id          = saved.id,
                        contributor = saved.contributor,
                        amount      = saved.amount,
                        createdAt   = saved.createdAt
                    )
                )
            }
        }
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private fun SpendingItem.toPending() = PendingSpendingItem(
        date        = date,
        shopper     = shopper,
        name        = name,
        quantity    = quantity,
        price       = price,
        description = description,
        createdAt   = ZonedDateTime.now().toString()
    )

    private fun PendingSpendingItem.toSpendingItem() = SpendingItem(
        date        = date,
        shopper     = shopper,
        name        = name,
        quantity    = quantity,
        price       = price,
        description = description
        // id and createdAt intentionally omitted → default "" → not serialized
    )
}
