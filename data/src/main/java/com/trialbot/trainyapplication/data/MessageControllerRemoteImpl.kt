package com.trialbot.trainyapplication.data

import com.trialbot.trainyapplication.data.remote.chatServer.ChatApi
import com.trialbot.trainyapplication.domain.interfaces.MessageControllerRemote
import com.trialbot.trainyapplication.domain.model.MessageDTO
import com.trialbot.trainyapplication.domain.model.MessageWithAuthUser
import com.trialbot.trainyapplication.domain.model.UserMessageWithoutIcon

class MessageControllerRemoteImpl(private val chatApi: ChatApi) : MessageControllerRemote {

    override suspend fun getAllMessages(): List<MessageDTO>? {
        return try {
            chatApi.getAllMessages()
        } catch (e: Exception) {
//            Log.e(MyApp.ERROR_LOG_TAG, "MessageControllerRemote.getAllMessages() -> ${e.localizedMessage}")
            null
        }
    }

    override suspend fun getMessagesByUser(user: UserMessageWithoutIcon): List<MessageDTO>? {
        return try {
            chatApi.getMessagesByUser(user.id, user.username)
        } catch (e: Exception) {
//            Log.e(MyApp.ERROR_LOG_TAG, "MessageControllerRemote.getMessagesByUser() -> ${e.localizedMessage}")
            null
        }
    }

    override suspend fun saveMessage(message: MessageWithAuthUser): Boolean {
        return try {
            chatApi.saveMessage(message)
            true
        } catch (e: Exception) {
//            Log.e(MyApp.ERROR_LOG_TAG, "MessageControllerRemote.saveMessage() -> ${e.localizedMessage}")
            false
        }
    }
}