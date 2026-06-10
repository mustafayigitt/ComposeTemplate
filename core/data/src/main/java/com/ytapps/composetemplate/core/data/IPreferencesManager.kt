package com.ytapps.composetemplate.core.data

import kotlinx.coroutines.flow.StateFlow

/**
 * Interface for managing user preferences with DataStore.
 * Provides both synchronous (cached) and asynchronous access patterns.
 */
interface IPreferencesManager {
    // Synchronous getters (for interceptor compatibility - uses cached values)
    fun getAccessToken(): String?

    fun getRefreshToken(): String?

    fun getTokenType(): String?

    fun getUUID(): String?

    fun hasUser(): Boolean

    // Async setters (DataStore operations)
    suspend fun setAccessToken(accessToken: String)

    suspend fun setRefreshToken(refreshToken: String)

    suspend fun setTokenType(tokenType: String)

    suspend fun setUUID(uuid: String)

    suspend fun clear()

    // Flow-based reactive access
    val accessTokenFlow: StateFlow<String?>
    val refreshTokenFlow: StateFlow<String?>
    val tokenTypeFlow: StateFlow<String?>
    val uuidFlow: StateFlow<String?>
}
