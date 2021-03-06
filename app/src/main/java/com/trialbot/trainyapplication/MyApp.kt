package com.trialbot.trainyapplication

import android.app.Application
import com.trialbot.trainyapplication.data.di.dataModule
import com.trialbot.trainyapplication.di.appModule
import com.trialbot.trainyapplication.domain.di.domainModule
import com.trialbot.trainyapplication.utils.AndroidLoggingHandler
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            properties(mapOf("base_url" to MY_BASE_URL))
            androidContext(this@MyApp)
            modules(appModule, dataModule, domainModule)
        }

        AndroidLoggingHandler.setup()
    }

    companion object {
        // AVD
//        const val MY_BASE_URL = "https://10.0.2.2:5001/api/"
        // REAL PHONE
        const val MY_BASE_URL = "https://trialbot-chat-server.herokuapp.com/api/"

        const val SHARED_PREFS_AUTH_TAG = "user_auth_info"
    }
}