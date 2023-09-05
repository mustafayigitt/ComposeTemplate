package com.ytapps.composetemplate.core.api

import com.ytapps.composetemplate.data.local.IPreferencesManager
import com.ytapps.composetemplate.domain.repository.IAuthRepository
import dagger.Lazy
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.net.HttpURLConnection
import javax.inject.Inject

/**
 * Created by mustafa.yigit on 26/08/2023
 * mustafa.yt65@gmail.com
 */

class DefaultInterceptor @Inject constructor(
    private val prefs: IPreferencesManager,
    private val authRepository: Lazy<IAuthRepository>
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val tokenType = prefs.getTokenType()
        val accessToken = prefs.getAccessToken()

        val request = chain.request().newBuilder()
            .addHeader(HEADER_AUTHORIZATION, "$tokenType $accessToken")
            .build()

        val response = chain.proceed(request)
        if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
            val newToken = runBlocking {
                authRepository.get().refreshToken().data ?: ""
            }
            val authorizedRequest = request.newBuilder()
                .addHeader(HEADER_AUTHORIZATION, "$tokenType $newToken")
                .build()
            return chain.proceed(authorizedRequest)
        }
        return response
    }

    companion object {
        const val HEADER_AUTHORIZATION = "Authorization"
    }
}