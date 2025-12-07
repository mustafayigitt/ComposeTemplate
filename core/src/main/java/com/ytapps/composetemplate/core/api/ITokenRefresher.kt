package com.ytapps.composetemplate.core.api

/**
 * Interface definition for token refreshing mechanism
 */
interface ITokenRefresher {
    suspend fun refreshToken(): Result<String>
}
