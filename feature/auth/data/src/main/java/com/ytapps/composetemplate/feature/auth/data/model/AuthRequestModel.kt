package com.ytapps.composetemplate.feature.auth.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AuthRequestModel(
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String,
)
