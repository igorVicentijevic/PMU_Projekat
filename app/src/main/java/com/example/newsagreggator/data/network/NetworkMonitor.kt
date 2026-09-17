package com.example.newsagreggator.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

interface NetworkMonitor {
    val isOnline: Flow<Boolean>
}

class AndroidNetworkMonitor @Inject constructor(
    @ApplicationContext context: Context,
) : NetworkMonitor {
    private val connectivityManager =
        context.applicationContext.getSystemService(ConnectivityManager::class.java)

    override val isOnline: Flow<Boolean> = callbackFlow {
        val validatedNetworks = mutableSetOf<Network>()

        fun hasValidatedInternet(
            capabilities: NetworkCapabilities?,
        ): Boolean = capabilities?.run {
            hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } == true

        fun updateNetwork(network: Network, isValidated: Boolean) {
            val online = synchronized(validatedNetworks) {
                if (isValidated) {
                validatedNetworks += network
                } else {
                validatedNetworks -= network
                }
                validatedNetworks.isNotEmpty()
            }
            trySend(online)
        }

        connectivityManager.activeNetwork?.let { activeNetwork ->
            updateNetwork(
                network = activeNetwork,
                isValidated = hasValidatedInternet(
                connectivityManager.getNetworkCapabilities(activeNetwork)
                ),
            )
        } ?: trySend(false)

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                updateNetwork(
                network = network,
                isValidated = hasValidatedInternet(networkCapabilities),
                )
            }

            override fun onLost(network: Network) {
                updateNetwork(network, isValidated = false)
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)

        connectivityManager.activeNetwork?.let { activeNetwork ->
            updateNetwork(
                network = activeNetwork,
                isValidated = hasValidatedInternet(
                connectivityManager.getNetworkCapabilities(
                    activeNetwork
                )
                )
            )
        }

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()
}

class InMemoryNetworkMonitor(
    initialOnlineState: Boolean = true,
) : NetworkMonitor {
    override val isOnline = MutableStateFlow(initialOnlineState)
}
