package com.trialbot.trainyapplication.domain

import android.content.SharedPreferences
import android.util.Log
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.data.AuthenticationControllerLocal
import com.trialbot.trainyapplication.data.AuthenticationControllerRemote
import com.trialbot.trainyapplication.data.model.UserAuth
import com.trialbot.trainyapplication.data.model.UserAuthId
import com.trialbot.trainyapplication.data.model.UserFull
import com.trialbot.trainyapplication.data.model.UserWithoutPassword
import com.trialbot.trainyapplication.data.remote.chatServer.ChatApi

class AuthUseCases(
    sharedPreferences: SharedPreferences,
    chatApi: ChatApi
) {

    private val authControllerRemote = AuthenticationControllerRemote(chatApi)
    private val authControllerLocal = AuthenticationControllerLocal(sharedPreferences)

    suspend fun register(username: String, password: String): UserFull? {
        val user = authControllerRemote.createUser(UserAuth(username, password)) ?: return null

        if (saveLocallyIfRemoteSuccessful(UserAuthId(user.id, user.username, password))) {
            return user
        }
        else {
            return null
        }
    }

    suspend fun login(username: String, password: String): UserWithoutPassword? {
        val user = authControllerRemote.getUserByAuthData(UserAuth(username, password)) ?: return null

        if (!saveLocallyIfRemoteSuccessful(UserAuthId(user.id, user.username, password))) return null

        return user
    }

    fun getLocalData(): UserAuthId? {
        return authControllerLocal.getCredentials()
    }

    private fun saveLocallyIfRemoteSuccessful(
        user: UserAuthId,
    ): Boolean {
        if (!saveLocalData(user)) {
            Log.e(
                MyApp.ERROR_LOG_TAG, "AuthUseCases.login() -> cannot locally save user auth data"
            )
            return false
        }
        return true
    }

    private fun saveLocalData(userAuthId: UserAuthId): Boolean {
        return authControllerLocal.saveCredentials(userAuthId)
    }
}