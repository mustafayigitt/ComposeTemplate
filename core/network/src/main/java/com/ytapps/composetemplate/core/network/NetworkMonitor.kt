package com.ytapps.composetemplate.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

sealed class NetworkStatus {
    object Available : NetworkStatus()

    object Lost : NetworkStatus()

    object Unavailable : NetworkStatus()
}

@Singleton
class NetworkMonitor
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        private val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkStatus: Flow<NetworkStatus> =
            callbackFlow {
                val callback =
                    object : ConnectivityManager.NetworkCallback() {
                        override fun onAvailable(network: Network) {
                            trySend(NetworkStatus.Available)
                        }

                        override fun onLost(network: Network) {
                            trySend(NetworkStatus.Lost)
                        }

                        override fun onUnavailable() {
                            trySend(NetworkStatus.Unavailable)
                        }
                    }

                val request =
                    NetworkRequest
                        .Builder()
                        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        .build()

                connectivityManager.registerNetworkCallback(request, callback)

                // Initial state
                val activeNetwork = connectivityManager.activeNetwork
                if (activeNetwork == null) {
                    trySend(NetworkStatus.Unavailable)
                }

                awaitClose {
                    connectivityManager.unregisterNetworkCallback(callback)
                }
            }.distinctUntilChanged()
    }
