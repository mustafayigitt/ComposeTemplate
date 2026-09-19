package com.ytapps.composetemplate.core.network

/**
 * Supplies environment-specific network configuration without coupling the transport layer to
 * the module that stores it.
 *
 * Optional infrastructure can contribute one implementation through Hilt. When no contribution
 * exists, [DefaultNetworkConfigProvider] keeps the generated project buildable and gives the
 * developer an explicit endpoint to replace.
 */
interface NetworkConfigProvider {
    val baseUrl: String
    val certificatePinningEnabled: Boolean
    val certificatePins: List<String>
}

internal object DefaultNetworkConfigProvider : NetworkConfigProvider {
    override val baseUrl: String = "https://example.com/"
    override val certificatePinningEnabled: Boolean = false
    override val certificatePins: List<String> = emptyList()
}
