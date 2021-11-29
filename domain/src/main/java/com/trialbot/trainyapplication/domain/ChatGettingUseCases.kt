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

    suspend fun getDialogNameSuspend(chatId: Long): String? {
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

    fun getDialogName(username: String, chatName: String): String? {
        return try {
            val names = chatName.split(DIALOG_DIVIDER)
            if (username == names[0]) names[1] else names[0]
        } catch (e: Exception) {
            null
        }
    }

    fun getDialogNameInverted(username: String, chatName: String): String? {
        return try {
            val names = chatName.split(DIALOG_DIVIDER)
            if (username == names[0]) names[0] else names[1]
        } catch (e: Exception) {
            null
        }
    }

    fun getDialogIcon(username: String, chat: ChatDetails): Int? {
        return try {
            val names = chat.name.split(DIALOG_DIVIDER)
            if (username == names[0]) chat.secondIcon else chat.icon
        } catch (e: Exception) {
            null
        }
    }

    fun getDialogIconInverted(username: String, chat: ChatDetails): Int? {
        return try {
            val names = chat.name.split(DIALOG_DIVIDER)
            if (username == names[0]) chat.icon else chat.secondIcon
        } catch (e: Exception) {
            null
        }
    }
}