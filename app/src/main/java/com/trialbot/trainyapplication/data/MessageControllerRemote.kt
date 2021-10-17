package com.trialbot.trainyapplication.data

import android.util.Log
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.data.model.MessageDTO
import com.trialbot.trainyapplication.data.model.MessageWithAuthUser
import com.trialbot.trainyapplication.data.model.UserMessageWithoutIcon
import com.trialbot.trainyapplication.data.remote.chatServer.ChatApi

class MessageControllerRemote(private val chatApi: ChatApi) {

    suspend fun getAllMessages(): List<MessageDTO>? {
        return try {
            chatApi.getAllMessages()
        } catch (e: Exception) {
            Log.e(MyApp.ERROR_LOG_TAG, "MessageControllerRemote.getAllMessages() -> ${e.localizedMessage}")
            null
        }
    }

    suspend fun getMessagesByUser(user: UserMessageWithoutIcon): List<MessageDTO>? {
        return try {
            chatApi.getMessagesByUser(user.id, user.username)
        } catch (e: Exception) {
            Log.e(MyApp.ERROR_LOG_TAG, "MessageControllerRemote.getMessagesByUser() -> ${e.localizedMessage}")
            null
        }
    }

    suspend fun saveMessage(message: MessageWithAuthUser): Boolean {
        return try {
            chatApi.saveMessage(message)
            true
        } catch (e: Exception) {
            Log.e(MyApp.ERROR_LOG_TAG, "MessageControllerRemote.saveMessage() -> ${e.localizedMessage}")
            false
        }
    }
}