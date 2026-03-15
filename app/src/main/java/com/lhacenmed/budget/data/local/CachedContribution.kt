package com.lhacenmed.budget.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lhacenmed.budget.data.model.BudgetContribution

@Entity(tableName = "cached_contributions")
data class CachedContribution(
    @PrimaryKey val id: String,
    val contributor: String,
    val amount: Float,
    val createdAt: String = ""
) {
    fun toContribution() = BudgetContribution(
        id          = id,
        contributor = contributor,
        amount      = amount,
        createdAt   = createdAt
    )
}

fun BudgetContribution.toCached() = CachedContribution(
    id          = id,
    contributor = contributor,
    amount      = amount,
    createdAt   = createdAt
)
