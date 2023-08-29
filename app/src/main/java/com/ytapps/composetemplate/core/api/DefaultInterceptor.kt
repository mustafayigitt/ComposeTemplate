package com.ytapps.composetemplate.core.api

import com.ytapps.composetemplate.data.local.PreferencesManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Created by mustafa.yigit on 26/08/2023
 * mustafa.yt65@gmail.com
 */

class DefaultInterceptor @Inject constructor(
    private val prefs: PreferencesManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val tokenType = prefs.getTokenType()
        val accessToken = prefs.getAccessToken()

        val request = chain.request().newBuilder()
            .addHeader(HEADER_AUTHORIZATION, "$tokenType $accessToken")
            .build()

        return chain.proceed(request)
    }

    companion object {
        const val HEADER_AUTHORIZATION = "Authorization"
    }
}