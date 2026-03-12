package com.lhacenmed.budget.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A grocery to-do item scoped to a user.
 *
 * Reset strategy: [checkedDate] stores the ISO date ("yyyy-MM-dd") on which the item was
 * last checked. `isChecked = checkedDate == today` — so it auto-resets the next day
 * purely by data, with no background jobs or app-open triggers.
 */
@Entity(tableName = "grocery_items")
data class GroceryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val name: String,
    val checkedDate: String? = null, // null = unchecked; "yyyy-MM-dd" = checked on that date
    val createdAt: String            // ISO string for ordering
)
