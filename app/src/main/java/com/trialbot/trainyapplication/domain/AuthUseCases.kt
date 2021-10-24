package com.trialbot.trainyapplication.domain

import android.content.SharedPreferences
import android.util.Log
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.data.AuthenticationControllerRemote
import com.trialbot.trainyapplication.data.model.UserAuth
import com.trialbot.trainyapplication.data.model.UserLocal
import com.trialbot.trainyapplication.data.model.UserWithoutPassword
import com.trialbot.trainyapplication.data.remote.chatServer.ChatApi

class AuthUseCases(
    sharedPreferences: SharedPreferences,
    chatApi: ChatApi
) {

    private val authControllerRemote = AuthenticationControllerRemote(chatApi)
    val localDataUseCases = LocalDataUseCases(sharedPreferences)

    suspend fun register(username: String, password: String): UserWithoutPassword? {
        val user = authControllerRemote.createUser(UserAuth(username, password)) ?: return null

        if (saveLocallyIfRemoteSuccessful(UserLocal(user.id, user.username, password, user.icon))) {
            return UserWithoutPassword(user.id, user.icon, user.username, user.isOnline, user.lastDate)
        }
        else {
            return null
        }
    }

    suspend fun login(username: String, password: String): UserWithoutPassword? {
        val user = authControllerRemote.getUserByAuthData(UserAuth(username, password)) ?: return null

        if (!saveLocallyIfRemoteSuccessful(UserLocal(user.id, user.username, password, user.icon))) return null

        return user
    }

    private fun saveLocallyIfRemoteSuccessful(
        user: UserLocal,
    ): Boolean {
        if (!localDataUseCases.saveLocalData(user)) {
            Log.e(
                MyApp.ERROR_LOG_TAG, "AuthUseCases.login() -> cannot locally save user auth data"
            )
            return false
        }
        return true
    }
}