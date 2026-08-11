package com.ytapps.composetemplate.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ytapps.composetemplate.core.database.entity.ExampleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExampleDao {
    @Query("SELECT * FROM examples")
    fun getAllExamples(): Flow<List<ExampleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExample(example: ExampleEntity)

    @Delete
    suspend fun deleteExample(example: ExampleEntity)

    @Query("DELETE FROM examples")
    suspend fun clearAll()
}
