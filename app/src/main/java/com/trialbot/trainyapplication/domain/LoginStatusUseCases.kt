package com.trialbot.trainyapplication.domain

import android.content.SharedPreferences

const val SAVE_LOGIN_STATUS_TAG = "is_login_successful"

class LoginStatusUseCases(
    private val sharedPrefs: SharedPreferences
) {

    fun saveLoginStatus(status: Boolean) {
        with (sharedPrefs.edit()) {
            putBoolean(SAVE_LOGIN_STATUS_TAG, status)
            apply()
        }
    }

    fun getLoginStatus(): Boolean = sharedPrefs.getBoolean(SAVE_LOGIN_STATUS_TAG, false)
}