package com.lhacenmed.budget.feature.home.navigation.di

import com.lhacenmed.budget.core.navigation.IBottomBarItem
import com.lhacenmed.budget.core.navigation.INavigationItem
import com.lhacenmed.budget.feature.home.navigation.HomeRoute
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.IntoSet
import dagger.multibindings.StringKey

@Module
@InstallIn(SingletonComponent::class)
internal object HomeNavigationModule {
    @Provides
    @IntoSet
    fun provideHomeRoute(): INavigationItem = HomeRoute

    @Provides
    @IntoMap
    @StringKey("1")
    fun provideHomeBottomBarItem(): IBottomBarItem = HomeRoute
}
