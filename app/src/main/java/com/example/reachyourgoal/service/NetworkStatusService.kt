package com.example.reachyourgoal.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.ContextCompat.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkStatusService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var isInternetAvailable: Boolean = false

    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()

    private val connectivityManager =
        getSystemService(context, ConnectivityManager::class.java) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            isInternetAvailable = true
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            isInternetAvailable = false
        }
    }

    init {
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

    fun isInternetAvailable(): Boolean = isInternetAvailable
}