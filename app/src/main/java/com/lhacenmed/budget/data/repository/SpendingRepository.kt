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
     * Offline-first:
     * 1. Save to Room immediately (always works)
     * 2. Try Supabase — on success, remove from Room
     * 3. On failure — stays in Room for syncPending()
     */
    suspend fun addItem(item: SpendingItem) {
        val localId = dao.insert(item.toPending()).toInt()
        runCatching {
            supabase.from("spending_items").insert(item)
            dao.deleteById(localId)
        }
        // failure is silent — item stays in queue, syncPending() will retry
    }

    /** Drains the local queue to Supabase. Called when connectivity is restored. */
    suspend fun syncPending() {
        dao.getAll().forEach { pending ->
            runCatching {
                // id and createdAt are left as defaults ("") → @EncodeDefault(NEVER)
                // ensures they are excluded from serialization → Supabase generates them
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
        description = description
        // id and createdAt intentionally omitted → default "" → not serialized by @EncodeDefault(NEVER)
    )
}
