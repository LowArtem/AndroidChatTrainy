package com.trialbot.trainyapplication.data

import com.trialbot.trainyapplication.data.remote.chatServer.ChatApi
import com.trialbot.trainyapplication.domain.interfaces.ChatControllerRemote
import com.trialbot.trainyapplication.domain.model.*
import com.trialbot.trainyapplication.domain.utils.logE

class ChatControllerRemoteImpl(private val chatApi: ChatApi) : ChatControllerRemote {
    override suspend fun getAllChatsByUser(userId: Long): List<ChatInfo>? {
        return try {
            chatApi.getAllChatsByUser(userId)
        } catch (e: Exception) {
            logE(e.localizedMessage ?: "Some error")
            null
        }
    }

    override suspend fun getChat(id: Long): ChatDetails? {
        return try {
            chatApi.getChat(id)
        } catch (e: Exception) {
            logE(e.localizedMessage ?: "Some error")
            null
        }
    }

    override suspend fun getChatMembers(chatId: Long): List<UserWithoutPassword>? {
        return try {
            chatApi.getChatMembers(chatId)
        } catch (e: Exception) {
            logE(e.localizedMessage ?: "Some error")
            null
        }
    }

    override suspend fun createChat(newChat: ChatCreating): Long {
        return try {
            chatApi.createChat(newChat)
        } catch (e: Exception) {
            logE(e.localizedMessage ?: "Some error")
            -1
        }
    }

    override suspend fun createDialog(creatorId: Long, secondUserId: Long): Long {
        return try {
            chatApi.createDialog(creatorId, secondUserId)
        } catch (e: Exception) {
            logE(e.localizedMessage ?: "Some error")
            -1
        }
    }

    override suspend fun updateChat(id: Long, chat: ChatUpdating): Boolean {
        return try {
            chatApi.updateChat(id, chat)
            true
        } catch (e: Exception) {
            logE(e.localizedMessage ?: "Some error")
            false
        }
    }

    override suspend fun deleteChat(chatId: Long, currentUserId: Long): Boolean {
        return try {
            chatApi.deleteChat(chatId, currentUserId)
            true
        } catch (e: Exception) {
            logE(e.localizedMessage ?: "Some error")
            false
        }
    }

    override suspend fun findChats(name: String): List<ChatInfo>? {
        return try {
            chatApi.findChats(name)
        } catch (e: Exception) {
            logE(e.localizedMessage ?: "Some error")
            null
        }
    }

    override suspend fun addMember(chatId: Long, userId: Long): Boolean {
        return try {
            chatApi.addMember(chatId, userId)
            true
        } catch (e: Exception) {
            logE(e.localizedMessage ?: "Some error")
            false
        }
    }

    override suspend fun deleteMember(chatId: Long, userId: Long): Boolean {
        return try {
            chatApi.deleteMember(chatId, userId)
            true
        } catch (e: Exception) {
            logE(e.localizedMessage ?: "Some error")
            false
        }
    }

    override suspend fun getAdminIds(chatId: Long): List<Long>? {
        return try {
            chatApi.getAdminIds(chatId)
        } catch (e: Exception) {
            logE(e.localizedMessage ?: "Some error")
            null
        }
    }

    override suspend fun addAdmin(chatId: Long, userId: Long): Boolean {
        return try {
            chatApi.addAdmin(chatId, userId)
            true
        } catch (e: Exception) {
            logE(e.localizedMessage ?: "Some error")
            false
        }
    }

    override suspend fun deleteAdmin(chatId: Long, adminId: Long): Boolean {
        return try {
            chatApi.deleteAdmin(chatId, adminId)
            true
        } catch (e: Exception) {
            logE(e.localizedMessage ?: "Some error")
            false
        }
    }
}