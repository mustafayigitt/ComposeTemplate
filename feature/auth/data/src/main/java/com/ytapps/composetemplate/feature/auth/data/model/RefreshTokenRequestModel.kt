package com.ytapps.composetemplate.feature.auth.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RefreshTokenRequestModel(
    @SerialName("refreshToken")
    val refreshToken: String,
)
