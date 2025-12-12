package com.ytapps.composetemplate.feature.list.navigation.di

import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.feature.list.navigation.ListRoute
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
