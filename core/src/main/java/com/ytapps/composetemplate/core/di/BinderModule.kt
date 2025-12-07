package com.ytapps.composetemplate.core.di

import com.ytapps.composetemplate.core.local.IPreferencesManager
import com.ytapps.composetemplate.core.local.PreferencesManager
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
}