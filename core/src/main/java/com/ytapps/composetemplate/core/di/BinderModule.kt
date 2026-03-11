package com.lhacenmed.budget.core.di

import com.lhacenmed.budget.core.local.IPreferencesManager
import com.lhacenmed.budget.core.local.PreferencesManager
import com.lhacenmed.budget.core.navigation.INavigationManager
import com.lhacenmed.budget.core.navigation.NavigationManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class BinderModule {

    @Binds
    abstract fun bindPreferencesManager(
        preferencesManager: PreferencesManager
    ): IPreferencesManager

    @Binds
    abstract fun bindNavigationManager(
        navigationManager: NavigationManager
    ): INavigationManager
}