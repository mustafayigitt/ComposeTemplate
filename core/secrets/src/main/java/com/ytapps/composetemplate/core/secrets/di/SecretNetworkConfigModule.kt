package com.ytapps.composetemplate.core.secrets.di

import com.ytapps.composetemplate.core.network.NetworkConfigProvider
import com.ytapps.composetemplate.core.secrets.SecretManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject

internal class SecretNetworkConfigProvider
    @Inject
    constructor() : NetworkConfigProvider {
        override val baseUrl: String
            get() = SecretManager.getBaseUrl()

        override val certificatePinningEnabled: Boolean
            get() = SecretManager.isCertificatePinningEnabled()

        override val certificatePins: List<String>
            get() = SecretManager.getCertificatePins()
    }

@Module
@InstallIn(SingletonComponent::class)
internal interface SecretNetworkConfigModule {
    @Binds
    @IntoSet
    fun bindSecretNetworkConfigProvider(
        provider: SecretNetworkConfigProvider,
    ): NetworkConfigProvider
}
