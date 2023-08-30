package com.ytapps.composetemplate.data.model

import com.google.gson.annotations.SerializedName

data class AuthResponseModel(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("expiresIn")
    val expiresIn: String,
    @SerializedName("tokenType")
    val tokenType: String
)
