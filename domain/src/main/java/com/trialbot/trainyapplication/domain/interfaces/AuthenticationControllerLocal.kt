package com.trialbot.trainyapplication.domain.interfaces

import com.trialbot.trainyapplication.domain.model.UserLocal

interface AuthenticationControllerLocal {

    fun saveCredentials(userLocal: UserLocal): Boolean

    fun getCredentials(): UserLocal?
}