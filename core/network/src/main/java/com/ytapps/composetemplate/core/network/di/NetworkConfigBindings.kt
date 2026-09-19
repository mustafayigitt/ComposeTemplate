package com.ytapps.composetemplate.core.network.di

import com.ytapps.composetemplate.core.network.NetworkConfigProvider
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds

@Module
@InstallIn(SingletonComponent::class)
internal interface NetworkConfigBindings {
    @Multibinds
    fun bindNetworkConfigProviders(): Set<NetworkConfigProvider>
}
