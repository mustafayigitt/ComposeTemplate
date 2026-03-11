package com.lhacenmed.budget.data.repository

import com.lhacenmed.budget.data.model.SpendingItem
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpendingRepository @Inject constructor(
    private val supabase: SupabaseClient
) {
    suspend fun getDays(): List<String> =
        supabase.from("spending_items")
            .select(columns = io.github.jan.supabase.postgrest.query.Columns.raw("date"))
            .decodeList<Map<String, String>>()
            .mapNotNull { it["date"] }
            .distinct()
            .sortedDescending()

    suspend fun getItemsForDay(date: String): List<SpendingItem> =
        supabase.from("spending_items")
            .select { filter { eq("date", date) }; order("created_at", Order.ASCENDING) }
            .decodeList()

    suspend fun addItem(item: SpendingItem) =
        supabase.from("spending_items").insert(item)

    suspend fun deleteItem(id: String) =
        supabase.from("spending_items").delete { filter { eq("id", id) } }
}
