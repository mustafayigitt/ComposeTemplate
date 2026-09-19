package com.ytapps.composetemplate.core.network.di

import com.ytapps.composetemplate.core.network.AuthInterceptor
import com.ytapps.composetemplate.core.network.BuildConfig
import com.ytapps.composetemplate.core.network.DefaultNetworkConfigProvider
import com.ytapps.composetemplate.core.network.NetworkConfigProvider
import com.ytapps.composetemplate.core.network.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.CertificatePinner
import okhttp3.HttpUrl.Companion.toHttpUrl
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
    fun provideNetworkConfig(
        providers: Set<@JvmSuppressWildcards NetworkConfigProvider>,
    ): NetworkConfigProvider {
        if (providers.isEmpty()) {
            return DefaultNetworkConfigProvider
        }
        check(providers.size == 1) {
            "Only one NetworkConfigProvider may be contributed, found ${providers.size}."
        }
        return providers.first()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator,
        networkConfig: NetworkConfigProvider,
    ): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    redactHeader(AuthInterceptor.HEADER_AUTHORIZATION)
                    redactHeader("Cookie")
                    redactHeader("Set-Cookie")
                    level =
                        if (BuildConfig.DEBUG) {
                            HttpLoggingInterceptor.Level.BODY
                        } else {
                            HttpLoggingInterceptor.Level.NONE
                        }
                },
            ).applyCertificatePinning(networkConfig)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        networkConfig: NetworkConfigProvider,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(networkConfig.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private fun OkHttpClient.Builder.applyCertificatePinning(
        networkConfig: NetworkConfigProvider,
    ): OkHttpClient.Builder {
        if (BuildConfig.DEBUG || !networkConfig.certificatePinningEnabled) {
            return this
        }

        require(networkConfig.certificatePins.size >= MIN_CERTIFICATE_PIN_COUNT) {
            "Release certificate pinning requires primary and backup SHA-256 pins."
        }

        val host = networkConfig.baseUrl.toHttpUrl().host
        val certificatePinnerBuilder = CertificatePinner.Builder()
        networkConfig.certificatePins.forEach { pin ->
            certificatePinnerBuilder.add(host, pin)
        }
        certificatePinner(certificatePinnerBuilder.build())
        return this
    }

    private const val MIN_CERTIFICATE_PIN_COUNT = 2
}
