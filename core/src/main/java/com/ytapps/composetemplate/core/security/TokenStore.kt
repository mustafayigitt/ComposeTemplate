package com.ytapps.composetemplate.core.security

import kotlinx.coroutines.flow.Flow

/**
 * Stores authentication tokens separately from regular user preferences.
 *
 * Implementations should use Android Keystore-backed encryption because access and
 * refresh tokens are credentials, not ordinary preferences.
 */
interface TokenStore {
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun getTokenType(): String?

    suspend fun setAccessToken(accessToken: String)
    suspend fun setRefreshToken(refreshToken: String)
    suspend fun setTokenType(tokenType: String)
    suspend fun clear()

    val accessTokenFlow: Flow<String?>
    val refreshTokenFlow: Flow<String?>
    val tokenTypeFlow: Flow<String?>
}
