package com.ytapps.composetemplate.feature.auth.data.model

import com.google.gson.annotations.SerializedName

internal data class RefreshTokenRequestModel(
    @SerializedName("refreshToken")
    val refreshToken: String,
)
