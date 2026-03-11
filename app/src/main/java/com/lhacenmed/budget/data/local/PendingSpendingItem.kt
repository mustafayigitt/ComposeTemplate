package com.lhacenmed.budget.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_spending_items")
data class PendingSpendingItem(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val date: String,
    val shopper: String,
    val name: String,
    val quantity: String,
    val price: Float,
    val description: String? = null,
    val createdAt: String
)
