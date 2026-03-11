package com.lhacenmed.budget.core.api

/**
 * Interface definition for token refreshing mechanism
 */
interface ITokenRefresher {
    suspend fun refreshToken(): Result<String>
}
