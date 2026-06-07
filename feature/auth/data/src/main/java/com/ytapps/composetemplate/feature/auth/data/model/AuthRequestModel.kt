package com.ytapps.composetemplate.feature.auth.data.model

import com.google.gson.annotations.SerializedName

internal data class AuthRequestModel(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
)
