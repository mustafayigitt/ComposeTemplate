package com.ytapps.composetemplate.core.data.security

import kotlinx.coroutines.flow.Flow

/**
 * Stores authentication tokens separately from regular user preferences.
 *
 * Implementations must avoid deprecated security APIs and should persist tokens
 * encrypted at rest with Android Keystore-backed keys.
 */
interface TokenStore {
    fun getTokens(): AuthTokens?

    fun getAccessToken(): String? = getTokens()?.accessToken

    fun getRefreshToken(): String? = getTokens()?.refreshToken

    fun getTokenType(): String = getTokens()?.tokenType ?: AuthTokens.DEFAULT_TOKEN_TYPE

    suspend fun saveTokens(tokens: AuthTokens)

    suspend fun clear()

    val tokensFlow: Flow<AuthTokens?>
}
