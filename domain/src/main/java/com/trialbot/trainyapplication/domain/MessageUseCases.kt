package com.trialbot.trainyapplication.domain

import com.trialbot.trainyapplication.domain.interfaces.MessageControllerRemote
import com.trialbot.trainyapplication.domain.model.MessageDTO
import com.trialbot.trainyapplication.domain.model.MessageWithAuthUser


class MessageUseCases(private val messageControllerRemote: MessageControllerRemote) {

    suspend fun sendMessage(message: MessageWithAuthUser): Boolean {
        val isSuccessful = messageControllerRemote.saveMessage(message)
        updateMessages()

        return isSuccessful
    }

    /**
     * Returns the new list of messages.
     * If list is the same, returns null.
     */
    suspend fun updateMessages(): List<MessageDTO> {
        return when (val gotMessages = messageControllerRemote.getAllMessages()) {
            null -> {
                emptyList()
            }
            else -> {
                gotMessages
            }
        }
    }
}