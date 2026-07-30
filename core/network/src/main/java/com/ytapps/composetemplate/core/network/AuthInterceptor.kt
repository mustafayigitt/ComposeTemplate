package com.ytapps.composetemplate.core.network

import com.ytapps.composetemplate.core.data.security.TokenStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Interceptor that adds authentication headers to requests.
 * This interceptor only handles adding headers - token refresh is handled by TokenAuthenticator.
 */
internal class AuthInterceptor
    @Inject
    constructor(
        private val tokenStore: TokenStore,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()

            val accessToken = tokenStore.getAccessToken()

            if (accessToken.isNullOrEmpty()) {
                return chain.proceed(originalRequest)
            }

            val authenticatedRequest =
                originalRequest
                    .newBuilder()
                    .header(HEADER_AUTHORIZATION, "${tokenStore.getTokenType()} $accessToken")
                    .build()

            return chain.proceed(authenticatedRequest)
        }

        companion object {
            const val HEADER_AUTHORIZATION = "Authorization"
        }
    }
