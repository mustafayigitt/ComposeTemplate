package com.ytapps.composetemplate.core.local

import kotlinx.coroutines.flow.Flow

/**
 * Created by mustafayigitt on 26/08/2023
 * mustafa.yt65@gmail.com
 * 
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
    val accessTokenFlow: Flow<String?>
    val refreshTokenFlow: Flow<String?>
    val tokenTypeFlow: Flow<String?>
    val uuidFlow: Flow<String?>
}