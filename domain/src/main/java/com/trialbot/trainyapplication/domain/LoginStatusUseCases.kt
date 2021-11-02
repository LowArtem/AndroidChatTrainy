package com.trialbot.trainyapplication.domain

import com.trialbot.trainyapplication.domain.interfaces.LoginStatusControllerLocal

class LoginStatusUseCases(private val loginStatusControllerLocal: LoginStatusControllerLocal) {

    fun saveLoginStatus(status: Boolean) {
        loginStatusControllerLocal.saveLoginStatus(status)
    }

    fun getLoginStatus(): Boolean {
        return loginStatusControllerLocal.getLoginStatus()
    }
}