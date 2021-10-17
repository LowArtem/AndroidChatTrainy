package com.trialbot.trainyapplication.data

import android.content.SharedPreferences
import android.util.Log
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.data.model.UserAuthId

const val SAVE_USERNAME_AUTH_TAG = "save_username_auth"
const val SAVE_PASSWORD_AUTH_TAG = "save_password_auth"
const val SAVE_ID_AUTH_TAG = "save_id_auth"

class AuthenticationControllerLocal(
    private val sharedPreferences: SharedPreferences
) {

    fun saveCredentials(userAuthId: UserAuthId): Boolean {
        return try {
            with(sharedPreferences.edit()) {
                putString(SAVE_USERNAME_AUTH_TAG, userAuthId.username)
                putString(SAVE_PASSWORD_AUTH_TAG, userAuthId.password)
                putLong(SAVE_ID_AUTH_TAG, userAuthId.id)
                apply()
            }
            true
        } catch (e: Exception) {
            Log.e(MyApp.ERROR_LOG_TAG, "AuthenticationControllerLocal.saveUserAuth() -> ${e.localizedMessage}")
            false
        }
    }

    fun getCredentials(): UserAuthId? {
        val username = sharedPreferences.getString(SAVE_USERNAME_AUTH_TAG, "")
        val password = sharedPreferences.getString(SAVE_PASSWORD_AUTH_TAG, "")
        val id = sharedPreferences.getLong(SAVE_ID_AUTH_TAG, 0L)

        if (username == null || username == "" || password == null || password == "" || id == 0L)
            return null

        return UserAuthId(id, username, password)
    }
}