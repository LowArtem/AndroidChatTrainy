package com.trialbot.trainyapplication.domain.interfaces

interface LoginStatusControllerLocal {

    fun saveLoginStatus(status: Boolean)

    fun getLoginStatus(): Boolean
}