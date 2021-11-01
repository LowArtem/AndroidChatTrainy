package com.trialbot.trainyapplication.utils

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.domain.utils.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

suspend fun isServerAvailable(connectivityManager: ConnectivityManager): Boolean = withContext(Dispatchers.IO) {
    if (connectivityManager.activeNetwork == null) return@withContext false

    try {
        trustAllCertificates()

        val url = URL(MyApp.MY_BASE_URL)
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        urlConnection.connectTimeout = 10 * 1000 // 10 s.
        urlConnection.allowUserInteraction = false
        urlConnection.connect()

        return@withContext urlConnection.responseCode == HttpURLConnection.HTTP_OK
    } catch (e: Exception) {
        logE(e.localizedMessage ?: "Some error")
        return@withContext false
    }
}

fun isInternetAvailable(connectivityManager: ConnectivityManager): Boolean {
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

@Suppress("UNCHECKED_CAST")
@SuppressLint("TrustAllX509TrustManager")
private fun trustAllCertificates() {
    try {
        val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(
            @SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                val acceptedIssuers: Array<Any?>
                    get() = arrayOfNulls(0)

                override fun checkClientTrusted(certs: Array<X509Certificate?>?, authType: String?) {}
                override fun checkServerTrusted(certs: Array<X509Certificate?>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return acceptedIssuers as Array<X509Certificate>
                }

            }
        )
        val sc: SSLContext = SSLContext.getInstance("SSL")
        sc.init(null, trustAllCerts, SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
        HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
    } catch (e: java.lang.Exception) {
    }
}