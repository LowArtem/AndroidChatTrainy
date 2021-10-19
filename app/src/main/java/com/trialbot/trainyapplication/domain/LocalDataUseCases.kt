package com.trialbot.trainyapplication.domain

import android.content.SharedPreferences
import com.trialbot.trainyapplication.data.AuthenticationControllerLocal
import com.trialbot.trainyapplication.data.model.UserAuthId

class LocalDataUseCases(sharedPreferences: SharedPreferences) {

    private val authControllerLocal = AuthenticationControllerLocal(sharedPreferences)

    fun getLocalData(): UserAuthId? {
        return authControllerLocal.getCredentials()
    }

    fun saveLocalData(userAuthId: UserAuthId): Boolean {
        return authControllerLocal.saveCredentials(userAuthId)
    }
}