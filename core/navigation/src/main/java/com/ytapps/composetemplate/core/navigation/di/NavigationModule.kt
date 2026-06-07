package com.ytapps.composetemplate.core.navigation.di

import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.navigation.NavigationManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NavigationModule {
    @Binds
    @Singleton
    abstract fun bindNavigationManager(navigationManager: NavigationManager): INavigationManager
}
