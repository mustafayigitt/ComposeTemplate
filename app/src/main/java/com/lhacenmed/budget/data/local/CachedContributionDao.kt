package com.lhacenmed.budget.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedContributionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CachedContribution>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CachedContribution)

    @Query("SELECT * FROM cached_contributions ORDER BY createdAt DESC")
    suspend fun getAll(): List<CachedContribution>
}
