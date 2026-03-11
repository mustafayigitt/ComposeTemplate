package com.lhacenmed.budget.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PendingItemDao {
    @Insert
    suspend fun insert(item: PendingSpendingItem): Long

    @Query("SELECT * FROM pending_spending_items")
    suspend fun getAll(): List<PendingSpendingItem>

    @Query("SELECT COUNT(*) FROM pending_spending_items")
    suspend fun count(): Int

    @Query("DELETE FROM pending_spending_items WHERE localId = :id")
    suspend fun deleteById(id: Int)
}
