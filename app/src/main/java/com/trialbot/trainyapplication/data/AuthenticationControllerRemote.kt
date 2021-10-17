package com.trialbot.trainyapplication.data

import android.util.Log
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.data.model.UserAuth
import com.trialbot.trainyapplication.data.model.UserFull
import com.trialbot.trainyapplication.data.model.UserWithoutIcon
import com.trialbot.trainyapplication.data.model.UserWithoutPassword
import com.trialbot.trainyapplication.data.remote.chatServer.ChatApi
import java.util.*

class AuthenticationControllerRemote(private val chatApi: ChatApi) {

    suspend fun getUserByAuthData(userAuth: UserAuth): UserWithoutPassword? {
        return try {
            chatApi.getUserByUsernameAndPassword(userAuth.username, userAuth.password)
        } catch (e: Exception) {
            Log.e(MyApp.ERROR_LOG_TAG, "AuthenticationControllerRemote.getUserByAuthData() -> ${e.localizedMessage}")
            null
        }
    }

    suspend fun createUser(userAuth: UserAuth): UserFull? {
        val user = UserFull(
            id = 0,
            icon = "string",
            username = userAuth.username,
            password = userAuth.password,
            isOnline = true,
            lastDate = Calendar.getInstance().time
        )

        return try {
            return chatApi.saveUser(user)
        } catch (e: Exception) {
            Log.e(MyApp.ERROR_LOG_TAG, "AuthenticationControllerRemote.createUser() -> ${e.localizedMessage}")
            null
        }
    }

    suspend fun updateUser(id: Long, user: UserFull): Boolean {
        return try {
            chatApi.updateUser(id, user)

            true
        } catch (e: Exception) {
            Log.e(MyApp.ERROR_LOG_TAG, "AuthenticationControllerRemote.updateUser() -> ${e.localizedMessage}")
            false
        }
    }

    suspend fun deleteUser(id: Long, userAuth: UserAuth): Boolean {
        val user = UserWithoutIcon(
            id = id,
            username = userAuth.username,
            password = userAuth.password,
            isOnline = false,
            lastDate = Calendar.getInstance().time
        )

        return try {
            chatApi.deleteUser(user)
            true
        } catch (e: Exception) {
            Log.e(MyApp.ERROR_LOG_TAG, "AuthenticationControllerRemote.deleteUser() -> ${e.localizedMessage}")
            false
        }
    }
}