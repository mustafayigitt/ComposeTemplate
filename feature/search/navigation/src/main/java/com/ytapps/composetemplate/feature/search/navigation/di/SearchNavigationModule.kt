package com.ytapps.composetemplate.feature.search.navigation.di

import com.ytapps.composetemplate.core.navigation.IBottomBarItem
import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.feature.search.navigation.SearchRoute
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.IntoSet
import dagger.multibindings.StringKey

@Module
@InstallIn(SingletonComponent::class)
internal object SearchNavigationModule {
    @Provides
    @IntoSet
    fun provideSearchRoute(): INavigationItem = SearchRoute

    @Provides
    @IntoMap
    @StringKey("2")
    fun provideSearchBottomBarItem(): IBottomBarItem = SearchRoute
}
