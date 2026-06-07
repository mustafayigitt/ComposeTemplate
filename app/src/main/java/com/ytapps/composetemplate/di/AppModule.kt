package com.ytapps.composetemplate.di

import com.ytapps.composetemplate.core.util.SecretManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideBaseUrl(): String = SecretManager.getBaseUrl()
}
