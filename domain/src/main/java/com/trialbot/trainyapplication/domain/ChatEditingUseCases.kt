package com.trialbot.trainyapplication.domain

import com.trialbot.trainyapplication.domain.interfaces.ChatControllerRemote
import com.trialbot.trainyapplication.domain.model.ChatCreating
import com.trialbot.trainyapplication.domain.model.ChatDetails
import com.trialbot.trainyapplication.domain.model.ChatInfo

class ChatEditingUseCases(private val chatControllerRemote: ChatControllerRemote) {

    suspend fun createChat(newChat: ChatCreating): ChatDetails? {
        val chatId = chatControllerRemote.createChat(newChat)
        if (chatId == -1L) return null

        return chatControllerRemote.getChat(chatId)
    }

    suspend fun createDialog(creatorId: Long, secondUserId: Long): ChatInfo? {
        val dialogId = chatControllerRemote.createDialog(creatorId, secondUserId)
        if (dialogId == -1L) return null

        val dialog = chatControllerRemote.getChat(dialogId) ?: return null
        return ChatInfo(dialogId, dialog.name, dialog.icon, true)
    }
}