package com.ytapps.composetemplate.core.secrets.di

import com.ytapps.composetemplate.core.network.NetworkConfigProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal interface SecretNetworkConfigModule {
    @Binds
    @IntoSet
    fun bindSecretNetworkConfigProvider(provider: SecretNetworkConfigProvider): NetworkConfigProvider
}
