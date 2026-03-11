package com.lhacenmed.budget.feature.profile.navigation.di

import com.lhacenmed.budget.core.navigation.IBottomBarItem
import com.lhacenmed.budget.core.navigation.INavigationItem
import com.lhacenmed.budget.feature.profile.navigation.ProfileRoute
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.IntoSet
import dagger.multibindings.StringKey

@Module
@InstallIn(SingletonComponent::class)
internal object ProfileNavigationModule {
    @Provides
    @IntoSet
    fun provideProfileRoute(): INavigationItem = ProfileRoute

    @Provides
    @IntoMap
    @StringKey("3")
    fun provideProfileBottomBarItem(): IBottomBarItem = ProfileRoute
}
