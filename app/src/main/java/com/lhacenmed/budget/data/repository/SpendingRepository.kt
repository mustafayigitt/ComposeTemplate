package com.lhacenmed.budget.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.lhacenmed.budget.data.local.AppDatabase
import com.lhacenmed.budget.data.local.PendingSpendingItem
import com.lhacenmed.budget.data.model.BudgetContribution
import com.lhacenmed.budget.data.model.SpendingItem
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@RequiresApi(Build.VERSION_CODES.O)
@Singleton
class SpendingRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val db: AppDatabase
) {
    private val dao get() = db.pendingItemDao()

    // ── Spending ──────────────────────────────────────────────────────────────

    suspend fun getDays(): List<String> =
        supabase.from("spending_items")
            .select(io.github.jan.supabase.postgrest.query.Columns.raw("date"))
            .decodeList<Map<String, String>>()
            .mapNotNull { it["date"] }
            .distinct()
            .sortedDescending()

    suspend fun getItemsForDay(date: String): List<SpendingItem> =
        supabase.from("spending_items")
            .select { filter { eq("date", date) }; order("created_at", Order.ASCENDING) }
            .decodeList()

    suspend fun getAllSpending(): List<SpendingItem> =
        supabase.from("spending_items").select().decodeList()

    /**
     * Offline-first add:
     * 1. Save to local Room queue immediately
     * 2. Try to push to Supabase
     * 3. On success → remove from local queue
     * 4. On failure → stays in queue for later sync
     */
    suspend fun addItem(item: SpendingItem) {
        val pending = item.toPending()
        val localId = dao.insert(pending).toInt()
        runCatching {
            supabase.from("spending_items").insert(item)
            dao.deleteById(localId)
        }
        // failure is silent — item stays in queue, syncPending() will retry
    }

    /**
     * Called when connectivity is restored.
     * Drains the local queue to Supabase.
     */
    suspend fun syncPending() {
        dao.getAll().forEach { pending ->
            runCatching {
                supabase.from("spending_items").insert(pending.toSpendingItem())
                dao.deleteById(pending.localId)
            }
        }
    }

    suspend fun pendingCount(): Int = dao.count()

    suspend fun deleteItem(id: String) =
        supabase.from("spending_items").delete { filter { eq("id", id) } }

    // ── Budget contributions ──────────────────────────────────────────────────

    suspend fun getContributions(): List<BudgetContribution> =
        supabase.from("budget_contributions")
            .select { order("created_at", Order.DESCENDING) }
            .decodeList()

    suspend fun addContribution(contribution: BudgetContribution) =
        supabase.from("budget_contributions").insert(contribution)

    // ── Mappers ───────────────────────────────────────────────────────────────

    private fun SpendingItem.toPending() = PendingSpendingItem(
        date = date,
        shopper = shopper,
        name = name,
        quantity = quantity,
        price = price,
        description = description,
        createdAt = ZonedDateTime.now().toString()
    )

    private fun PendingSpendingItem.toSpendingItem() = SpendingItem(
        date = date,
        shopper = shopper,
        name = name,
        quantity = quantity,
        price = price,
        description = description,
        createdAt = createdAt
    )
}
