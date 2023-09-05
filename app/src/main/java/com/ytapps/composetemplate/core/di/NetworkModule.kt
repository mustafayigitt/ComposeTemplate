package com.ytapps.composetemplate.core.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ytapps.composetemplate.BuildConfig
import com.ytapps.composetemplate.core.api.DefaultInterceptor
import com.ytapps.composetemplate.data.local.IPreferencesManager
import com.ytapps.composetemplate.data.remote.AuthService
import com.ytapps.composetemplate.domain.repository.IAuthRepository
import com.ytapps.composetemplate.util.Constants
import dagger.Lazy
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
 * Created by mustafa.yigit on 26/08/2023
 * mustafa.yt65@gmail.com
 */

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return httpLoggingInterceptor
    }

    @Provides
    @Singleton
    fun provideDefaultInterceptor(
        prefs: IPreferencesManager,
        authRepository: Lazy<IAuthRepository>
    ): DefaultInterceptor {
        return DefaultInterceptor(prefs, authRepository)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        defaultInterceptor: DefaultInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(defaultInterceptor)
            .addInterceptor(loggingInterceptor)
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
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }

    @Provides
    fun provideAuthService(retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

}