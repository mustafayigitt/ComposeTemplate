package com.lhacenmed.budget.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedSpendingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CachedSpendingItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CachedSpendingItem)

    @Query("SELECT * FROM cached_spending_items ORDER BY createdAt ASC")
    suspend fun getAll(): List<CachedSpendingItem>

    @Query("SELECT * FROM cached_spending_items WHERE date = :date ORDER BY createdAt ASC")
    suspend fun getByDate(date: String): List<CachedSpendingItem>

    @Query("SELECT DISTINCT date FROM cached_spending_items ORDER BY date DESC")
    suspend fun getDates(): List<String>

    @Query("DELETE FROM cached_spending_items WHERE id = :id")
    suspend fun deleteById(id: String)
}
