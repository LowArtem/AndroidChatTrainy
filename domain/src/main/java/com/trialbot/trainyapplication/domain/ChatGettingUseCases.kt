package com.trialbot.trainyapplication.domain

import com.trialbot.trainyapplication.domain.interfaces.ChatControllerRemote
import com.trialbot.trainyapplication.domain.model.ChatDetails
import com.trialbot.trainyapplication.domain.model.ChatInfo
import com.trialbot.trainyapplication.domain.model.UserWithoutPassword
import com.trialbot.trainyapplication.domain.utils.DIALOG_DIVIDER
import com.trialbot.trainyapplication.domain.utils.logE

class ChatGettingUseCases(
    private val chatControllerRemote: ChatControllerRemote,
    private val localDataUseCases: LocalDataUseCases
) {
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

    suspend fun getChatMembers(chatId: Long): List<UserWithoutPassword> {
        return chatControllerRemote.getChatMembers(chatId) ?: emptyList()
    }

    suspend fun findChatsByName(name: String): List<ChatInfo>? {
        val foundedChats = chatControllerRemote.findChats(name)
        if (foundedChats != null && foundedChats.isEmpty()) return null

        return foundedChats
    }

    suspend fun getDialogName(chatId: Long): String? {
        try {
            val chat = chatControllerRemote.getChat(chatId) ?: return null
            val currentUser = localDataUseCases.getLocalData() ?: return null
            val dialogNames = chat.name.split(DIALOG_DIVIDER)

            return if (currentUser.username == dialogNames[0]) dialogNames[1] else dialogNames[0]

        } catch (e: Exception) {
            logE(e.localizedMessage)
            return null
        }
    }
}