package com.ytapps.composetemplate.feature.profile.navigation.di

import com.ytapps.composetemplate.core.navigation.IBottomBarItem
import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.feature.profile.navigation.ProfileRoute
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
