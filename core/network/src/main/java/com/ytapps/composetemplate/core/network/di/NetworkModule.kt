package com.ytapps.composetemplate.core.network.di

import com.ytapps.composetemplate.core.network.AuthInterceptor
import com.ytapps.composetemplate.core.network.TokenAuthenticator
import com.ytapps.composetemplate.core.secrets.SecretManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            redactHeader(HEADER_AUTHORIZATION)
            redactHeader(HEADER_COOKIE)
            redactHeader(HEADER_SET_COOKIE)
            redactHeader(HEADER_API_KEY)
            redactHeader(HEADER_AUTH_TOKEN)
            // Keep network logging disabled by default in the shared network module.
            // Apps can replace this binding if they want opt-in debug logging.
            level = HttpLoggingInterceptor.Level.NONE
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(tokenAuthenticator)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(SecretManager.getBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private const val HEADER_AUTHORIZATION = "Authorization"
    private const val HEADER_COOKIE = "Cookie"
    private const val HEADER_SET_COOKIE = "Set-Cookie"
    private const val HEADER_API_KEY = "X-Api-Key"
    private const val HEADER_AUTH_TOKEN = "X-Auth-Token"
}
