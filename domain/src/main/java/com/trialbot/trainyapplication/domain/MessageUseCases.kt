package com.trialbot.trainyapplication.domain

import com.trialbot.trainyapplication.domain.interfaces.MessageControllerRemote
import com.trialbot.trainyapplication.domain.model.MessageDTO
import com.trialbot.trainyapplication.domain.model.MessageWithAuthUser

/**
 * Single class to save the messages
 */
class MessageUseCases(private val messageControllerRemote: MessageControllerRemote) {

    private var _messages: List<MessageDTO>? = null

    suspend fun sendMessage(message: MessageWithAuthUser): Boolean {
        val isSuccessful = messageControllerRemote.saveMessage(message)
        updateMessages()

        return isSuccessful
    }

    /**
     * Returns the new list of messages.
     * If list is the same, returns null.
     */
    suspend fun updateMessages(): List<MessageDTO>? {
        val gotMessages = messageControllerRemote.getAllMessages()

        return when {
            gotMessages == null -> {
                _messages = emptyList()
                _messages
            }
            gotMessages.last() != _messages?.last() -> {
                _messages = gotMessages
                _messages
            }
            else -> {
                null
            }
        }
    }
}