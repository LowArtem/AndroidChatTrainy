package com.trialbot.trainyapplication.data

import com.trialbot.trainyapplication.data.remote.chatServer.ChatApi
import com.trialbot.trainyapplication.domain.interfaces.MessageControllerRemote
import com.trialbot.trainyapplication.domain.model.MessageDTO
import com.trialbot.trainyapplication.domain.model.MessageWithAuthUser
import com.trialbot.trainyapplication.domain.model.UserMessageWithoutIcon
import com.trialbot.trainyapplication.domain.utils.logE

class MessageControllerRemoteImpl(private val chatApi: ChatApi) : MessageControllerRemote {

    override suspend fun getAllMessages(): List<MessageDTO>? {
        return try {
            chatApi.getAllMessages()
        } catch (e: Exception) {
            logE(e.localizedMessage ?: "Some error")
            null
        }
    }

    override suspend fun getMessagesByUser(user: UserMessageWithoutIcon): List<MessageDTO>? {
        return try {
            chatApi.getMessagesByUser(user.id, user.username)
        } catch (e: Exception) {
            logE(e.localizedMessage ?: "Some error")
            null
        }
    }

    override suspend fun saveMessage(message: MessageWithAuthUser): Boolean {
        return try {
            chatApi.saveMessage(message)
            true
        } catch (e: Exception) {
            logE(e.localizedMessage ?: "Some error")
            false
        }
    }
}