package com.ytapps.composetemplate.core.api

import com.ytapps.composetemplate.core.local.IPreferencesManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Interceptor that adds authentication headers to requests.
 * This interceptor only handles adding headers - token refresh is handled by TokenAuthenticator.
 */
class AuthInterceptor @Inject constructor(
    private val prefs: IPreferencesManager
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        val tokenType = prefs.getTokenType()
        val accessToken = prefs.getAccessToken()
        
        // If no token available, proceed without auth header
        if (accessToken.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }
        
        val authenticatedRequest = originalRequest.newBuilder()
            .header(HEADER_AUTHORIZATION, "$tokenType $accessToken")
            .build()
        
        return chain.proceed(authenticatedRequest)
    }

    companion object {
        const val HEADER_AUTHORIZATION = "Authorization"
    }
}
