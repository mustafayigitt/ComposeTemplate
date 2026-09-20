package com.ytapps.composetemplate.core.secrets.di

import com.ytapps.composetemplate.core.network.NetworkConfigProvider
import com.ytapps.composetemplate.core.secrets.SecretManager
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
