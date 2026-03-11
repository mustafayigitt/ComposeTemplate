package com.lhacenmed.budget.feature.list.navigation.di

import com.lhacenmed.budget.core.navigation.INavigationItem
import com.lhacenmed.budget.feature.list.navigation.ListRoute
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal object ListNavigationModule {
    @Provides
    @IntoSet
    fun provideListRoute(): INavigationItem = ListRoute
}
