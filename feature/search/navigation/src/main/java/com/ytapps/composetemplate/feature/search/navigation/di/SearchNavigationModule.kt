package com.lhacenmed.budget.feature.search.navigation.di

import com.lhacenmed.budget.core.navigation.IBottomBarItem
import com.lhacenmed.budget.core.navigation.INavigationItem
import com.lhacenmed.budget.feature.search.navigation.SearchRoute
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
