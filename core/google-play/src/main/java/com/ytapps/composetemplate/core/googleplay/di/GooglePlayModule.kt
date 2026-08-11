package com.ytapps.composetemplate.core.googleplay.di

import android.content.Context
import com.ytapps.composetemplate.core.config.IConfigManager
import com.ytapps.composetemplate.core.googleplay.ReviewManager
import com.ytapps.composetemplate.core.googleplay.UpdateManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GooglePlayModule {
    @Provides
    @Singleton
    fun provideReviewManager(
        @ApplicationContext context: Context,
    ): ReviewManager = ReviewManager(context)

    @Provides
    @Singleton
    fun provideUpdateManager(
        @ApplicationContext context: Context,
        configManager: IConfigManager,
    ): UpdateManager = UpdateManager(context, configManager)
}
