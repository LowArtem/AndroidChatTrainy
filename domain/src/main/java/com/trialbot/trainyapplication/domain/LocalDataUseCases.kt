package com.trialbot.trainyapplication.domain


import com.trialbot.trainyapplication.domain.interfaces.AuthenticationControllerLocal
import com.trialbot.trainyapplication.domain.model.UserLocal

class LocalDataUseCases(private val authControllerLocal: AuthenticationControllerLocal) {

    fun getLocalData(): UserLocal? {
        return authControllerLocal.getCredentials()
    }

    fun saveLocalData(userLocal: UserLocal): Boolean {
        return authControllerLocal.saveCredentials(userLocal)
    }
}