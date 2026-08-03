package com.ytapps.composetemplate.core.network.di

import com.ytapps.composetemplate.core.network.AuthInterceptor
import com.ytapps.composetemplate.core.network.BuildConfig
import com.ytapps.composetemplate.core.secrets.SecretManager
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
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level =
                        if (BuildConfig.DEBUG) {
                            HttpLoggingInterceptor.Level.BODY
                        } else {
                            HttpLoggingInterceptor.Level.NONE
                        }
                },
            ).applyCertificatePinning()
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    private val baseUrl: String
        get() = SecretManager.getBaseUrl()

    private fun OkHttpClient.Builder.applyCertificatePinning(): OkHttpClient.Builder {
        if (BuildConfig.DEBUG || !SecretManager.isCertificatePinningEnabled()) {
            return this
        }

        val pins = SecretManager.getCertificatePins()
        require(pins.size >= MIN_CERTIFICATE_PIN_COUNT) {
            "Release certificate pinning requires primary and backup SHA-256 pins."
        }

        val host = baseUrl.toHttpUrl().host
        val certificatePinnerBuilder = CertificatePinner.Builder()
        pins.forEach { pin ->
            certificatePinnerBuilder.add(host, pin)
        }
        certificatePinner(certificatePinnerBuilder.build())
        return this
    }

    private const val MIN_CERTIFICATE_PIN_COUNT = 2
}
