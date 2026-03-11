package com.lhacenmed.budget.feature.auth.data.model

import com.google.gson.annotations.SerializedName

internal data class AuthResponseModel(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("expiresIn")
    val expiresIn: String,
    @SerializedName("tokenType")
    val tokenType: String
)
