package com.trialbot.trainyapplication.domain

import com.trialbot.trainyapplication.domain.interfaces.ChatControllerRemote
import com.trialbot.trainyapplication.domain.model.ChatDetails
import com.trialbot.trainyapplication.domain.model.ChatInfo
import com.trialbot.trainyapplication.domain.model.UserWithoutPassword

class ChatGettingUseCases(private val chatControllerRemote: ChatControllerRemote) {
    suspend fun getAllChatsByUser(userId: Long): List<ChatInfo>? {
        return chatControllerRemote.getAllChatsByUser(userId)
    }

    suspend fun openChat(chatId: Long): ChatDetails? {
        return chatControllerRemote.getChat(chatId)
    }

    suspend fun checkIsAdmin(chatId: Long, userId: Long): Boolean? {
        val admins = chatControllerRemote.getAdminIds(chatId)
        if (userId < 0) return null

        return admins?.contains(userId)
    }

    suspend fun getAdminIds(chatId: Long): List<Long> {
        return chatControllerRemote.getAdminIds(chatId) ?: emptyList()
    }

    suspend fun checkIsCreator(chatId: Long, userId: Long): Boolean? {
        val chat = chatControllerRemote.getChat(chatId) ?: return null
        if (userId < 0) return null

        return (chat.creatorId == userId || chat.secondDialogMemberId == userId)
    }

    suspend fun getChatMembers(chatId: Long): List<UserWithoutPassword>? {
        return chatControllerRemote.getChatMembers(chatId)
    }

    suspend fun findChatsByName(name: String): List<ChatInfo>? {
        val foundedChats = chatControllerRemote.findChats(name)
        if (foundedChats != null && foundedChats.isEmpty()) return null

        return foundedChats
    }
}