package com.ytapps.composetemplate.core.network

import com.ytapps.composetemplate.core.common.getOrNull
import com.ytapps.composetemplate.core.data.security.AuthTokens
import com.ytapps.composetemplate.core.data.security.TokenStore
import dagger.Lazy
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp Authenticator that handles 401 responses by refreshing the access token.
 *
 * Why Authenticator instead of Interceptor?
 * - Authenticator is specifically designed for handling authentication challenges
 * - OkHttp automatically retries the request with the new credentials
 * - It prevents infinite retry loops with built-in response count checking
 * - Cleaner separation: Interceptor adds headers, Authenticator handles 401s
 *
 * Thread Safety:
 * - Uses synchronized block to prevent multiple simultaneous token refresh requests
 * - Checks if token was already refreshed by another thread
 */
@Singleton
internal class TokenAuthenticator
    @Inject
    constructor(
        private val tokenStore: TokenStore,
        private val tokenRefresher: Lazy<ITokenRefresher>,
    ) : Authenticator {
        private val lock = Any()

        override fun authenticate(
            route: Route?,
            response: Response,
        ): Request? {
            if (responseCount(response) >= MAX_RETRY_COUNT) {
                return null
            }

            val currentToken = tokenStore.getAccessToken()

            synchronized(lock) {
                val latestToken = tokenStore.getAccessToken()

                if (latestToken != currentToken && !latestToken.isNullOrEmpty()) {
                    return buildRequestWithToken(response.request, latestToken)
                }

                val refreshedTokens = refreshTokens()

                return if (refreshedTokens != null) {
                    buildRequestWithToken(response.request, refreshedTokens.accessToken)
                } else {
                    null
                }
            }
        }

        private fun refreshTokens(): AuthTokens? =
            try {
                runBlocking {
                    val tokens = tokenRefresher.get().refreshTokens().getOrNull()
                    if (tokens != null) {
                        tokenStore.saveTokens(tokens)
                    }
                    tokens
                }
            } catch (e: Exception) {
                Timber.e(e, "Token refresh failed")
                null
            }

        private fun buildRequestWithToken(
            request: Request,
            token: String,
        ): Request =
            request
                .newBuilder()
                .header(HEADER_AUTHORIZATION, "${tokenStore.getTokenType()} $token")
                .build()

        private fun responseCount(response: Response): Int {
            var count = 1
            var priorResponse = response.priorResponse
            while (priorResponse != null) {
                count++
                priorResponse = priorResponse.priorResponse
            }
            return count
        }

        companion object {
            private const val MAX_RETRY_COUNT = 3
            private const val HEADER_AUTHORIZATION = "Authorization"
        }
    }
