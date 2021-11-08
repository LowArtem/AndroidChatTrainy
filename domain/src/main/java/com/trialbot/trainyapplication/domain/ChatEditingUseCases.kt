package com.trialbot.trainyapplication.domain

import com.trialbot.trainyapplication.domain.interfaces.ChatControllerRemote
import com.trialbot.trainyapplication.domain.model.ChatCreating
import com.trialbot.trainyapplication.domain.model.ChatInfo
import com.trialbot.trainyapplication.domain.model.ChatUpdating

class ChatEditingUseCases(private val chatControllerRemote: ChatControllerRemote) {

    suspend fun createChat(newChat: ChatCreating): Long? {
        val chatId = chatControllerRemote.createChat(newChat)
        if (chatId == -1L) return null

        return chatId
    }

    suspend fun createDialog(creatorId: Long, secondUserId: Long): ChatInfo? {
        val dialogId = chatControllerRemote.createDialog(creatorId, secondUserId)
        if (dialogId == -1L) return null

        val dialog = chatControllerRemote.getChat(dialogId) ?: return null
        return ChatInfo(dialogId, dialog.name, dialog.icon, dialog.secondIcon, true)
    }

    suspend fun updateChatIcon(chatId: Long, iconId: Int): Boolean {
        val chat = chatControllerRemote.getChat(chatId) ?: return false
        val newChat = chat.copy(icon = iconId)
        return chatControllerRemote.updateChat(chatId, ChatUpdating(chatId, newChat.name, newChat.about, newChat.icon, newChat.creatorId))
    }

    suspend fun updateChatAbout(chatId: Long, about: String): Boolean {
        val chat = chatControllerRemote.getChat(chatId) ?: return false
        val newChat = chat.copy(about = about)
        return chatControllerRemote.updateChat(chatId, ChatUpdating(chatId, newChat.name, newChat.about, newChat.icon, newChat.creatorId))
    }

    suspend fun updateChatName(chatId: Long, name: String): Boolean {
        val chat = chatControllerRemote.getChat(chatId) ?: return false
        val newChat = chat.copy(name = name)
        return chatControllerRemote.updateChat(chatId, ChatUpdating(chatId, newChat.name, newChat.about, newChat.icon, newChat.creatorId))
    }

    suspend fun addAdmin(chatId: Long, userId: Long): Boolean {
        return chatControllerRemote.addAdmin(chatId, userId)
    }

    suspend fun removeAdmin(chatId: Long, userId: Long): Boolean {
        return chatControllerRemote.deleteAdmin(chatId, userId)
    }

    suspend fun addMember(chatId: Long, userId: Long): Boolean {
        return chatControllerRemote.addMember(chatId, userId)
        // Здесь можно послать пользователю notification, что он добавлен в чат и обновить у него список чатов
    }

    suspend fun removeMember(chatId: Long, userId: Long): Boolean {
        return chatControllerRemote.deleteMember(chatId, userId)
        // Можно подумать над настройкой для чата (вступать только по приглашению)
    }
}