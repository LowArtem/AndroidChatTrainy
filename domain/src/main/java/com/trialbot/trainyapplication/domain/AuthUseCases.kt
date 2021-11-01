package com.trialbot.trainyapplication.domain

import com.trialbot.trainyapplication.domain.interfaces.AuthenticationControllerRemote
import com.trialbot.trainyapplication.domain.model.UserAuth
import com.trialbot.trainyapplication.domain.model.UserLocal
import com.trialbot.trainyapplication.domain.model.UserWithoutPassword
import com.trialbot.trainyapplication.domain.utils.logE

class AuthUseCases(
    private val authControllerRemote: AuthenticationControllerRemote,
    private val localDataUseCases: LocalDataUseCases
) {
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
            logE("cannot locally save user auth data")
            return false
        }
        return true
    }
}