package com.ytapps.composetemplate.core.network

import com.ytapps.composetemplate.core.common.Result

/**
 * Interface definition for token refreshing mechanism
 */
interface ITokenRefresher {
    suspend fun refreshToken(): Result<String>
}
