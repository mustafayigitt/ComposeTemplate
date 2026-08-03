package com.ytapps.composetemplate.core.common

/**
 * Contract used by the network layer to refresh expired access tokens.
 */
interface ITokenRefresher {
    suspend fun refreshToken(): Result<String>
}
