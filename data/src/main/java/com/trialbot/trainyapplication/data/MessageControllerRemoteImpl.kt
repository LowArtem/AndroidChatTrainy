package com.trialbot.trainyapplication.data

import com.trialbot.trainyapplication.data.remote.chatServer.ChatApi
import com.trialbot.trainyapplication.domain.interfaces.MessageControllerRemote
import com.trialbot.trainyapplication.domain.model.MessageDTO
import com.trialbot.trainyapplication.domain.model.MessageWithAuthUser
import com.trialbot.trainyapplication.domain.model.UserMessageWithoutIcon
import com.trialbot.trainyapplication.domain.utils.logE

class MessageControllerRemoteImpl(private val chatApi: ChatApi) : MessageControllerRemote {

    override suspend fun getAllMessages(chatId: Long): List<MessageDTO>? {
        return try {
            chatApi.getAllMessages(chatId)
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

    override suspend fun getMessagesPage(
        chatId: Long,
        messageCount: Int,
        startIndex: Int
    ): List<MessageDTO>? {
        return try {
            chatApi.getMessagesPage(chatId, messageCount, startIndex)
        } catch (e: Exception) {
            logE(e.localizedMessage ?: "Some error")
            null
        }
    }

    override suspend fun saveMessage(chatId: Long, message: MessageWithAuthUser): Boolean {
        return try {
            chatApi.saveMessage(chatId, message)
            true
        } catch (e: Exception) {
            logE(e.localizedMessage ?: "Some error")
            false
        }
    }

    override suspend fun deleteMessage(chatId: Long, messageId: Long, currentUserId: Long): Boolean {
        return try {
            chatApi.deleteMessage(chatId, messageId, currentUserId)
            true
        } catch (e: Exception) {
            logE(e.localizedMessage ?: "Some error")
            false
        }
    }
}