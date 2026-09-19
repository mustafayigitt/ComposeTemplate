package com.ytapps.composetemplate.core.network

internal object DefaultNetworkConfigProvider : NetworkConfigProvider {
    override val baseUrl: String = "https://example.com/"
    override val certificatePinningEnabled: Boolean = false
    override val certificatePins: List<String> = emptyList()
}
