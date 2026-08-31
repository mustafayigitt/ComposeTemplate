package com.ytapps.composetemplate.core.secrets.initializer

import com.ytapps.composetemplate.core.common.initializer.AppInitializer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal interface SecretsInitializerModule {
    @Binds
    @IntoSet
    fun bindSecretsInitializer(initializer: SecretsInitializer): AppInitializer
}
