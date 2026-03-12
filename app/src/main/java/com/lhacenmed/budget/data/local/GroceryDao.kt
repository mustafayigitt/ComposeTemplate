package com.lhacenmed.budget.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryDao {

    @Query("SELECT * FROM grocery_items WHERE userId = :userId ORDER BY createdAt ASC")
    fun getItems(userId: String): Flow<List<GroceryItem>>

    @Insert
    suspend fun insert(item: GroceryItem): Long

    @Query("UPDATE grocery_items SET checkedDate = :date WHERE id = :id")
    suspend fun setCheckedDate(id: Int, date: String?)

    @Query("UPDATE grocery_items SET name = :name WHERE id = :id")
    suspend fun updateName(id: Int, name: String)

    @Query("DELETE FROM grocery_items WHERE id = :id")
    suspend fun delete(id: Int)
}
