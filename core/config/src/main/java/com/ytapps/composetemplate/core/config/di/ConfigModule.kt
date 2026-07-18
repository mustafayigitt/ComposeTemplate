package com.ytapps.composetemplate.core.config.di

import com.ytapps.composetemplate.core.config.IConfigManager
import com.ytapps.composetemplate.core.config.LocalConfigProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ConfigModule {
    @Binds
    @Singleton
    abstract fun bindConfigManager(localConfigProvider: LocalConfigProvider): IConfigManager
}
