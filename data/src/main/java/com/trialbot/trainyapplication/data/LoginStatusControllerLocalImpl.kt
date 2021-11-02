package com.trialbot.trainyapplication.data

import android.content.SharedPreferences
import com.trialbot.trainyapplication.domain.interfaces.LoginStatusControllerLocal

class LoginStatusControllerLocalImpl(private val sharedPrefs: SharedPreferences) : LoginStatusControllerLocal {
    override fun saveLoginStatus(status: Boolean) {
        with (sharedPrefs.edit()) {
            putBoolean(SAVE_LOGIN_STATUS_TAG, status)
            apply()
        }
    }

    override fun getLoginStatus(): Boolean = sharedPrefs.getBoolean(SAVE_LOGIN_STATUS_TAG, false)

    companion object {
        const val SAVE_LOGIN_STATUS_TAG = "is_login_successful"
    }
}