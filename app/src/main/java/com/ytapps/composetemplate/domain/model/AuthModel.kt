package com.ytapps.composetemplate.domain.model

/**
 * Created by mustafa.yigit on 30/08/2023
 * mustafa.yt65@gmail.com
 */
data class AuthModel(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: String,
    val tokenType: String
)