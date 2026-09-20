package com.ytapps.composetemplate.core.network

/**
 * Supplies environment-specific network configuration without coupling the transport layer to
 * the module that stores it.
 *
 * Optional infrastructure can contribute one implementation through Hilt. When no contribution
 * exists, the network module uses an explicit default endpoint so the project remains buildable.
 */
interface NetworkConfigProvider {
    val baseUrl: String
    val certificatePinningEnabled: Boolean
    val certificatePins: List<String>
}
