package com.ytapps.composetemplate.feature.auth.domain.model

/**
 * Created by mustafayigitt on 30/08/2023
 * mustafa.yt65@gmail.com
 */
data class AuthModel(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: String,
    val tokenType: String
)