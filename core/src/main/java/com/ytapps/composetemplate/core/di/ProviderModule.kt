package com.ytapps.composetemplate.core.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ytapps.composetemplate.core.api.AuthInterceptor
import com.ytapps.composetemplate.core.api.TokenAuthenticator
import com.ytapps.composetemplate.core.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created by mustafayigitt on 26/08/2023
 * mustafa.yt65@gmail.com
 */

@Module
@InstallIn(SingletonComponent::class)
internal object ProviderModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            redactHeader(HEADER_AUTHORIZATION)
            redactHeader(HEADER_COOKIE)
            redactHeader(HEADER_SET_COOKIE)
            redactHeader(HEADER_API_KEY)
            redactHeader(HEADER_AUTH_TOKEN)
            // Keep network logging disabled by default in the shared core module.
            // Apps can replace this binding if they want opt-in debug logging.
            setLevel(HttpLoggingInterceptor.Level.NONE)
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)          // Adds auth headers
            .addInterceptor(loggingInterceptor)       // Logs requests/responses with sensitive headers redacted
            .authenticator(tokenAuthenticator)         // Handles 401 and refreshes token
            .build()
    }


    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat(Constants.DATE_PATTERN)
            .create()
    }

    @Provides
    @Singleton
    fun provideGsonConverterFactory(
        gson: Gson
    ): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }

    private const val HEADER_AUTHORIZATION = "Authorization"
    private const val HEADER_COOKIE = "Cookie"
    private const val HEADER_SET_COOKIE = "Set-Cookie"
    private const val HEADER_API_KEY = "X-Api-Key"
    private const val HEADER_AUTH_TOKEN = "X-Auth-Token"
}
