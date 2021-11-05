package com.trialbot.trainyapplication.domain.interfaces

import com.trialbot.trainyapplication.domain.model.*

interface ChatControllerRemote {

    suspend fun getAllChatsByUser(userId: Long): List<ChatInfo>?

    suspend fun getChat(id: Long): ChatDetails?

    suspend fun getChatMembers(chatId: Long): List<UserWithoutPassword>?

    suspend fun createChat(newChat: ChatCreating): Long

    suspend fun createDialog(creatorId: Long, secondUserId: Long): Long

    suspend fun updateChat(id: Long, chat: ChatUpdating): Boolean

    suspend fun deleteChat(chatId: Long, currentUserId: Long): Boolean

    suspend fun findChats(name: String): List<ChatInfo>?

    suspend fun addMember(chatId: Long, userId: Long): Boolean

    suspend fun deleteMember(chatId: Long, userId: Long): Boolean

    suspend fun getAdminIds(chatId: Long): List<Long>?

    suspend fun addAdmin(chatId: Long, userId: Long): Boolean

    suspend fun deleteAdmin(chatId: Long, adminId: Long): Boolean
}