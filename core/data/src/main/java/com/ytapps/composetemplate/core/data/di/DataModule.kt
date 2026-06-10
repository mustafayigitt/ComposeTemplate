package com.ytapps.composetemplate.core.data.di

import com.ytapps.composetemplate.core.data.IPreferencesManager
import com.ytapps.composetemplate.core.data.PreferencesManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {
    @Binds
    @Singleton
    abstract fun bindPreferencesManager(preferencesManager: PreferencesManager): IPreferencesManager
}
