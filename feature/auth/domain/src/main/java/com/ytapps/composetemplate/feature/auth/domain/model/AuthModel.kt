package com.ytapps.composetemplate.feature.auth.domain.model

data class AuthModel(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: String,
    val tokenType: String,
)
