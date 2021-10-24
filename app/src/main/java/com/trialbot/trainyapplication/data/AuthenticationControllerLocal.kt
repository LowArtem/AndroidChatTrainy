package com.trialbot.trainyapplication.data

import android.content.SharedPreferences
import android.util.Log
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.data.model.UserLocal

const val SAVE_USERNAME_AUTH_TAG = "save_username_auth"
const val SAVE_PASSWORD_AUTH_TAG = "save_password_auth"
const val SAVE_ID_AUTH_TAG = "save_id_auth"
const val SAVE_USER_ICON_TAG = "save_user_icon"

class AuthenticationControllerLocal(
    private val sharedPreferences: SharedPreferences
) {

    fun saveCredentials(userLocal: UserLocal): Boolean {
        return try {
            with(sharedPreferences.edit()) {
                putString(SAVE_USERNAME_AUTH_TAG, userLocal.username)
                putString(SAVE_PASSWORD_AUTH_TAG, userLocal.password)
                putLong(SAVE_ID_AUTH_TAG, userLocal.id)
                putInt(SAVE_USER_ICON_TAG, userLocal.icon)
                apply()
            }
            true
        } catch (e: Exception) {
            Log.e(MyApp.ERROR_LOG_TAG, "AuthenticationControllerLocal.saveUserAuth() -> ${e.localizedMessage}")
            false
        }
    }

    fun getCredentials(): UserLocal? {
        val username = sharedPreferences.getString(SAVE_USERNAME_AUTH_TAG, "")
        val password = sharedPreferences.getString(SAVE_PASSWORD_AUTH_TAG, "")
        val id = sharedPreferences.getLong(SAVE_ID_AUTH_TAG, 0L)
        val iconId = sharedPreferences.getInt(SAVE_USER_ICON_TAG, -1)

        if (username == null || username == "" || password == null || password == "" || id == 0L)
            return null

        return UserLocal(id, username, password, iconId)
    }
}