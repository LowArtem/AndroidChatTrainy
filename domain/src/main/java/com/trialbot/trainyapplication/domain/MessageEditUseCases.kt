package com.trialbot.trainyapplication.domain

import com.trialbot.trainyapplication.domain.interfaces.ChatControllerRemote
import com.trialbot.trainyapplication.domain.interfaces.MessageControllerRemote

class MessageEditUseCases(
    private val chatControllerRemote: ChatControllerRemote,
    private val messageControllerRemote: MessageControllerRemote
) {
    suspend fun deleteMessage(chatId: Long, messageId: Long, currentUserId: Long): Boolean {
        val chatCreatorId = chatControllerRemote.getChat(chatId)?.creatorId ?: return false
        val admins = chatControllerRemote.getAdminIds(chatId) ?: return false

        if (!admins.contains(currentUserId) && chatCreatorId != currentUserId) return false

        return messageControllerRemote.deleteMessage(chatId, messageId, currentUserId)
    }
}