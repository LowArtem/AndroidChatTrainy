package com.trialbot.trainyapplication

import android.app.Application
import com.google.gson.GsonBuilder
import com.trialbot.trainyapplication.data.remote.chatServer.ChatApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager



class MyApp: Application() {
    lateinit var api: ChatApi
        private set

    override fun onCreate() {
        super.onCreate()

        configureRetrofit()
    }

    private fun configureRetrofit() {
        val client = OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            if (BuildConfig.DEBUG)
                ignoreAllSSLErrors()
        }.build()

        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(MY_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        api = retrofit.create(ChatApi::class.java)
    }

    private fun OkHttpClient.Builder.ignoreAllSSLErrors(): OkHttpClient.Builder {
        val naiveTrustManager = object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            override fun checkClientTrusted(certs: Array<X509Certificate>, authType: String) = Unit
            override fun checkServerTrusted(certs: Array<X509Certificate>, authType: String) = Unit
        }

        val insecureSocketFactory = SSLContext.getInstance("SSL").apply {
            val trustAllCerts = arrayOf<TrustManager>(naiveTrustManager)
            init(null, trustAllCerts, SecureRandom())
        }.socketFactory

        sslSocketFactory(insecureSocketFactory, naiveTrustManager)
        hostnameVerifier(HostnameVerifier { _, _ -> true })
        return this
    }

    companion object {
        const val MY_BASE_URL = "https://10.0.2.2:5001/api/"
        const val DEBUG_LOG_TAG = "AAAA"
        const val ERROR_LOG_TAG = "Application_Error"
        const val SHARED_PREFS_AUTH_TAG = "user_auth_info"
    }
}