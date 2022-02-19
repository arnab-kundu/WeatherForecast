package com.arnab.network

import android.content.Context
import android.net.ConnectivityManager

@Deprecated("unused")
object NetworkAvailability {

    private const val TAG = "NetworkAvailability"

    /**
     * Checks for internet connection availability
     * @return Boolean
     */
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager.allNetworks.isNotEmpty()) {
            return true
        }
        return false
    }


    /**
     * Checks for internet connection availability
     * @return Boolean
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.allNetworks.isNotEmpty()
    }
}