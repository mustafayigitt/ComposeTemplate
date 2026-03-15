package com.lhacenmed.budget.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lhacenmed.budget.data.model.SpendingItem

@Entity(tableName = "cached_spending_items")
data class CachedSpendingItem(
    @PrimaryKey val id: String,
    val date: String,
    val shopper: String,
    val name: String,
    val quantity: String,
    val price: Float,
    val description: String? = null,
    val createdAt: String = ""
) {
    fun toSpendingItem() = SpendingItem(
        id          = id,
        date        = date,
        shopper     = shopper,
        name        = name,
        quantity    = quantity,
        price       = price,
        description = description,
        createdAt   = createdAt
    )
}

fun SpendingItem.toCached() = CachedSpendingItem(
    id          = id,
    date        = date,
    shopper     = shopper,
    name        = name,
    quantity    = quantity,
    price       = price,
    description = description,
    createdAt   = createdAt
)
