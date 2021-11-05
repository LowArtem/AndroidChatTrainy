package com.trialbot.trainyapplication.data.di

import com.google.gson.GsonBuilder
import com.trialbot.trainyapplication.data.*
import com.trialbot.trainyapplication.data.remote.chatServer.ChatApi
import com.trialbot.trainyapplication.domain.interfaces.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

val dataModule = module {
    single {
        OkHttpClient.Builder().apply {
            addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
//            if (BuildConfig.DEBUG)
            ignoreAllSSLErrors()
        }.build()
    }

    single {
        GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()
    }

    single {
        Retrofit.Builder()
            .baseUrl(getProperty("base_url"))
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
            .create(ChatApi::class.java)
    }

    single<AuthenticationControllerLocal> {
        AuthenticationControllerLocalImpl(
            sharedPreferences = get()
        )
    }

    single<AuthenticationControllerRemote> {
        AuthenticationControllerRemoteImpl(
            chatApi = get()
        )
    }

    single<LoginStatusControllerLocal> {
        LoginStatusControllerLocalImpl(
            sharedPrefs = get()
        )
    }

    single<MessageControllerRemote> {
        MessageControllerRemoteImpl(
            chatApi = get()
        )
    }

    single<UserControllerRemote> {
        UserControllerRemoteImpl(
            chatApi = get()
        )
    }

    single<ChatControllerRemote> {
        ChatControllerRemoteImpl(
            chatApi = get()
        )
    }
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
    hostnameVerifier { _, _ -> true }
    return this
}