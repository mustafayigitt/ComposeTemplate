package com.ytapps.composetemplate.core.network

import com.ytapps.composetemplate.core.common.ITokenRefresher
import com.ytapps.composetemplate.core.common.getOrNull
import com.ytapps.composetemplate.core.data.IPreferencesManager
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
 *
 * Optional refreshing:
 * - Token refreshing is contributed by another module and may be absent entirely.
 * - When absent, a 401 is simply not retried; the network layer stays functional.
 */
@Singleton
internal class TokenAuthenticator
    @Inject
    constructor(
        private val prefs: IPreferencesManager,
        private val tokenRefreshers: Lazy<Set<@JvmSuppressWildcards ITokenRefresher>>,
    ) : Authenticator {
        private val lock = Any()

        override fun authenticate(
            route: Route?,
            response: Response,
        ): Request? {
            if (response
                    .request
                    .url
                    .encodedPath
                    .endsWith(AUTH_REFRESH_PATH)
            ) {
                return null
            }

            // Don't retry if we've already tried multiple times
            if (responseCount(response) >= MAX_RETRY_COUNT) {
                return null
            }

            val currentToken = prefs.getAccessToken()

            synchronized(lock) {
                // Check if token was already refreshed by another thread
                val latestToken = prefs.getAccessToken()

                if (latestToken != currentToken && !latestToken.isNullOrEmpty()) {
                    // Token was refreshed by another thread, retry with new token
                    return buildRequestWithToken(response.request, latestToken)
                }

                // Refresh the token
                val newToken = refreshToken()

                return if (!newToken.isNullOrEmpty()) {
                    buildRequestWithToken(response.request, newToken)
                } else {
                    // Token refresh failed or is not available, don't retry
                    null
                }
            }
        }

        private fun refreshToken(): String? {
            val refresher = resolveRefresher() ?: return null
            return try {
                // Note: runBlocking is acceptable here because:
                // 1. Authenticator.authenticate() is called on OkHttp's dispatcher thread, not the main thread
                // 2. The token refresh is a blocking operation by nature (we need the token before proceeding)
                // 3. This is the standard pattern for OkHttp Authenticator with suspend functions
                runBlocking {
                    refresher.refreshToken().getOrNull()
                }
            } catch (e: Exception) {
                Timber.e(e, "Token refresh failed")
                null
            }
        }

        /**
         * Resolves the single installed refresher, or null when the contributing module is
         * absent. Resolution is deferred until a 401 actually happens, which also keeps the
         * refresher out of the object graph that builds this authenticator.
         */
        private fun resolveRefresher(): ITokenRefresher? {
            val refreshers = tokenRefreshers.get()
            if (refreshers.isEmpty()) {
                Timber.d("No token refresher is installed; a 401 will not be retried.")
                return null
            }
            check(refreshers.size == 1) {
                "Exactly one ITokenRefresher may be contributed, but found ${refreshers.size}."
            }
            return refreshers.first()
        }

        private fun buildRequestWithToken(
            request: Request,
            token: String,
        ): Request {
            val tokenType = prefs.getTokenType() ?: "Bearer"
            return request
                .newBuilder()
                .header(HEADER_AUTHORIZATION, "$tokenType $token")
                .build()
        }

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
            private const val AUTH_REFRESH_PATH = "/auth/refresh"
            private const val HEADER_AUTHORIZATION = "Authorization"
        }
    }
