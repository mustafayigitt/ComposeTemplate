package com.ytapps.composetemplate.feature.home.navigation.di

import com.ytapps.composetemplate.core.navigation.IBottomBarItem
import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.feature.home.navigation.HomeRoute
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
