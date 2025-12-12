package com.ytapps.composetemplate.feature.splash.presentation.di

import com.ytapps.composetemplate.core.navigation.IScreenProvider
import com.ytapps.composetemplate.feature.splash.presentation.SplashScreenProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SplashModule {
    @Binds
    @IntoSet
    abstract fun bindSplashScreenProvider(
        provider: SplashScreenProvider
    ): IScreenProvider
}
