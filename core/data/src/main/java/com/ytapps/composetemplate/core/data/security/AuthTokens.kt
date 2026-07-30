package com.ytapps.composetemplate.core.data.security

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = DEFAULT_TOKEN_TYPE,
) {
    companion object {
        const val DEFAULT_TOKEN_TYPE = "Bearer"
    }
}
