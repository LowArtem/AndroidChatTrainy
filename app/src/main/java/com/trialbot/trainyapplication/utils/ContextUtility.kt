package com.trialbot.trainyapplication.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.trialbot.trainyapplication.MyApp
import java.net.HttpURLConnection
import java.net.URL


object ContextUtility {

    fun Context.isInternetAvailable(): Boolean {
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        if (capabilities != null) when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            }
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }

    fun Context.isServerAvailable(): Boolean {
        if ((getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetwork == null) return false

        return try {
            val url = URL(MyApp.MY_BASE_URL)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.connectTimeout = 10 * 1000 // 10 s.
            urlConnection.connect()

            urlConnection.responseCode == 200
        } catch (e: Exception) {
            false
        }
    }
}