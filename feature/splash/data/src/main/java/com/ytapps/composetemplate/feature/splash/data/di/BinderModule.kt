package com.ytapps.composetemplate.feature.splash.data.di

import com.ytapps.composetemplate.feature.splash.data.SplashRepository
import com.ytapps.composetemplate.feature.splash.domain.ISplashRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BinderModule {
    @Binds
    abstract fun bindSplashRepository(splashRepository: SplashRepository): ISplashRepository
}
