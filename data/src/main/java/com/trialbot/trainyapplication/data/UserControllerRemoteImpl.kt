package com.trialbot.trainyapplication.data

import com.trialbot.trainyapplication.data.remote.chatServer.ChatApi
import com.trialbot.trainyapplication.domain.interfaces.UserControllerRemote
import com.trialbot.trainyapplication.domain.model.UserFull
import com.trialbot.trainyapplication.domain.model.UserWithoutIcon
import java.util.*

class UserControllerRemoteImpl(private val chatApi: ChatApi) : UserControllerRemote {

    override suspend fun getUserIsOnline(id: Long): Boolean {
        return try {
            chatApi.getUserIsOnline(id)
        } catch (e: Exception) {
//            Log.e(MyApp.ERROR_LOG_TAG, "UserControllerRemote.getUserIsOnline() -> ${e.localizedMessage}")
            false
        }
    }

    override suspend fun getUserLastDate(id: Long): Date? {
        return try {
            chatApi.getUserLastDate(id)
        } catch (e: Exception) {
//            Log.e(MyApp.ERROR_LOG_TAG, "UserControllerRemote.getUserLastDate() -> ${e.localizedMessage}")
            null
        }
    }

    override suspend fun updateUser(id: Long, user: UserFull): Boolean {
        return try {
            chatApi.updateUser(id, user)
            true
        } catch (e: Exception) {
//            Log.e(MyApp.ERROR_LOG_TAG, "UserControllerRemote.updateUser() -> ${e.localizedMessage}")
            false
        }
    }

    override suspend fun deleteUser(user: UserWithoutIcon): Boolean {
        return try {
            chatApi.deleteUser(user)
            true
        } catch (e: Exception) {
//            Log.e(MyApp.ERROR_LOG_TAG, "UserControllerRemote.deleteUser() -> ${e.localizedMessage}")
            false
        }
    }
}