package com.ytapps.composetemplate.core.di

import com.ytapps.composetemplate.core.local.IPreferencesManager
import com.ytapps.composetemplate.core.local.PreferencesManager
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.navigation.NavigationManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class BinderModule {

    @Binds
    abstract fun bindPreferencesManager(
        preferencesManager: PreferencesManager
    ): IPreferencesManager

    @Binds
    abstract fun bindNavigationManager(
        navigationManager: NavigationManager
    ): INavigationManager
}