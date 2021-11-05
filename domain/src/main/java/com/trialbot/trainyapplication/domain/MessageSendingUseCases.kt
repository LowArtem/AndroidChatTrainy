package com.trialbot.trainyapplication.domain

import com.trialbot.trainyapplication.domain.interfaces.MessageControllerRemote
import com.trialbot.trainyapplication.domain.model.MessageDTO
import com.trialbot.trainyapplication.domain.model.MessageWithAuthUser


class MessageSendingUseCases(private val messageControllerRemote: MessageControllerRemote) {

    suspend fun sendMessage(chatId: Long, message: MessageWithAuthUser): Boolean {
        val isSuccessful = messageControllerRemote.saveMessage(chatId, message)
        getNewMessages(chatId)

        return isSuccessful
    }

    /**
     * Returns the new list of messages.
     * If list is the same, returns null.
     */
    suspend fun getNewMessages(chatId: Long): List<MessageDTO> {
        return when (val gotMessages = messageControllerRemote.getAllMessages(chatId)) {
            null -> {
                emptyList()
            }
            else -> {
                gotMessages
            }
        }
    }
}