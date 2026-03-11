package com.lhacenmed.budget.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpendingItem(
    val id: String = "",
    val date: String,           // "YYYY-MM-DD"
    val shopper: String,
    val name: String,
    val quantity: Float,
    val price: Float,
    @SerialName("created_at") val createdAt: String = ""
) {
    val total get() = quantity * price
}
