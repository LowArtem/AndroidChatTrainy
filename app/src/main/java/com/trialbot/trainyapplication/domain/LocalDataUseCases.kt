package com.trialbot.trainyapplication.domain

import android.content.SharedPreferences
import com.trialbot.trainyapplication.data.AuthenticationControllerLocal
import com.trialbot.trainyapplication.data.model.UserLocal

class LocalDataUseCases(sharedPreferences: SharedPreferences) {

    private val authControllerLocal = AuthenticationControllerLocal(sharedPreferences)

    fun getLocalData(): UserLocal? {
        return authControllerLocal.getCredentials()
    }

    fun saveLocalData(userLocal: UserLocal): Boolean {
        return authControllerLocal.saveCredentials(userLocal)
    }
}