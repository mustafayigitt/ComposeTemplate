package com.lhacenmed.budget.feature.splash.navigation.di

import com.lhacenmed.budget.core.navigation.INavigationItem
import com.lhacenmed.budget.feature.splash.navigation.SplashRoute
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object SplashNavigationModule {
    @Provides
    @IntoSet
    fun provideSplashRoute(): INavigationItem = SplashRoute

    @Provides
    @Singleton
    fun provideStartDestination(): INavigationItem {
        return SplashRoute
    }
}
