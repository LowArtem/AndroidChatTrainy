package com.trialbot.trainyapplication.di

import android.app.Application
import android.content.SharedPreferences
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.presentation.screen.chat.ChatViewModel
import com.trialbot.trainyapplication.presentation.screen.login.LoginViewModel
import com.trialbot.trainyapplication.presentation.screen.message.MessageViewModel
import com.trialbot.trainyapplication.presentation.screen.profile.ProfileViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.annotation.KoinReflectAPI
import org.koin.dsl.module

@OptIn(KoinReflectAPI::class)
val appModule = module {

    single{
        getSharedPrefs(androidApplication())
    }

    viewModel {
        MessageViewModel(
            loginStatus = get(),
            messageSendingUseCases = get(),
            localDataUseCases = get(),
            startStopRemoteActions = get()
        )
    }

    viewModel {
        LoginViewModel(
            loginStatus = get(),
            authUseCases = get(),
            localDataUseCases = get()
        )
    }

    viewModel {
        ProfileViewModel(
            editUserUseCases = get(),
            loginStatus = get(),
            localDataUseCases = get(),
            userStatusDataUseCases = get()
        )
    }

    viewModel {
        ChatViewModel(
            chatEditingUseCases = get(),
            chatGettingUseCases = get()
        )
    }
}


fun getSharedPrefs(androidApplication: Application): SharedPreferences {
    return androidApplication.getSharedPreferences(MyApp.SHARED_PREFS_AUTH_TAG,  android.content.Context.MODE_PRIVATE)
}