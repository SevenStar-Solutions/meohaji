package com.example.meohaji.search

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

class NetworkStatus {

    companion object {
        const val TYPE_WIFI = 1
        const val TYPE_MOBILE = 2
        const val TYPE_NOT_CONNECTED = 3


        fun getConnectivityStatus(context: Context): Int {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                val currentNetwork = connectivityManager.activeNetwork ?: return TYPE_NOT_CONNECTED
                val caps = connectivityManager.getNetworkCapabilities(currentNetwork) ?: return TYPE_NOT_CONNECTED

                return when {
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return TYPE_WIFI
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return TYPE_MOBILE
                    else -> return TYPE_NOT_CONNECTED
                }
            }

            return TYPE_NOT_CONNECTED
        }

    }

}