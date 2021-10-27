package com.trialbot.trainyapplication.data

import android.util.Log
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.data.model.UserFull
import com.trialbot.trainyapplication.data.model.UserWithoutIcon
import com.trialbot.trainyapplication.data.remote.chatServer.ChatApi

class UserControllerRemote(private val chatApi: ChatApi) {

    suspend fun getUserIsOnline(id: Long): Boolean {
        return try {
            chatApi.getUserIsOnline(id)
        } catch (e: Exception) {
            Log.e(MyApp.ERROR_LOG_TAG, "UserControllerRemote.getUserIsOnline() -> ${e.localizedMessage}")
            false
        }
    }

    suspend fun updateUser(id: Long, user: UserFull): Boolean {
        return try {
            chatApi.updateUser(id, user)
            true
        } catch (e: Exception) {
            Log.e(MyApp.ERROR_LOG_TAG, "UserControllerRemote.updateUser() -> ${e.localizedMessage}")
            false
        }
    }

    suspend fun deleteUser(user: UserWithoutIcon): Boolean {
        return try {
            chatApi.deleteUser(user)
            true
        } catch (e: Exception) {
            Log.e(MyApp.ERROR_LOG_TAG, "UserControllerRemote.deleteUser() -> ${e.localizedMessage}")
            false
        }
    }
}