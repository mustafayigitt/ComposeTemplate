package com.lhacenmed.budget.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.lhacenmed.budget.data.local.GroceryDao
import com.lhacenmed.budget.data.local.GroceryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

@RequiresApi(Build.VERSION_CODES.O)
@Singleton
class GroceryRepository @Inject constructor(private val dao: GroceryDao) {

    /** Emits items sorted unchecked-first; checked items sink to the bottom. */
    fun getItems(userId: String): Flow<List<GroceryItem>> {
        val today = LocalDate.now().toString()
        return dao.getItems(userId).map { items ->
            items.sortedWith(compareBy({ it.checkedDate == today }, { it.createdAt }))
        }
    }

    suspend fun addItem(userId: String, name: String) =
        dao.insert(GroceryItem(userId = userId, name = name, createdAt = ZonedDateTime.now().toString()))

    suspend fun toggleItem(item: GroceryItem) {
        val today = LocalDate.now().toString()
        dao.setCheckedDate(item.id, if (item.checkedDate == today) null else today)
    }

    suspend fun updateName(id: Int, name: String) = dao.updateName(id, name)

    suspend fun delete(id: Int) = dao.delete(id)
}
