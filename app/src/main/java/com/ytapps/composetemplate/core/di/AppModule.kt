package com.ytapps.composetemplate.core.di

import android.content.Context
import com.ytapps.composetemplate.data.local.PreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by mustafa.yigit on 26/08/2023
 * mustafa.yt65@gmail.com
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providePreferencesManager(
        @ApplicationContext appContext: Context
    ): PreferencesManager {
        return PreferencesManager(appContext)
    }
}

