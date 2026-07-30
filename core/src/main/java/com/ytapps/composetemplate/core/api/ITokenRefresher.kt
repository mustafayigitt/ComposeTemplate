package com.ytapps.composetemplate.core.api

import com.ytapps.composetemplate.core.security.AuthTokens

/**
 * Interface definition for token refreshing mechanism.
 *
 * Implementations should return the complete refreshed token set so callers can
 * persist access and refresh tokens atomically.
 */
interface ITokenRefresher {
    suspend fun refreshTokens(): Result<AuthTokens>
}
