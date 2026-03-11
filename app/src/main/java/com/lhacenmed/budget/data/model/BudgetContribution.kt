package com.lhacenmed.budget.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BudgetContribution(
    val id: String = "",
    val contributor: String,
    val amount: Float,
    @SerialName("created_at") val createdAt: String = ""
)
