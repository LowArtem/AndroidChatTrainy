package com.trialbot.trainyapplication.domain.interfaces

import com.trialbot.trainyapplication.domain.model.MessageDTO
import com.trialbot.trainyapplication.domain.model.MessageWithAuthUser
import com.trialbot.trainyapplication.domain.model.UserMessageWithoutIcon

interface MessageControllerRemote {

    suspend fun getAllMessages(chatId: Long): List<MessageDTO>?

    suspend fun getMessagesByUser(user: UserMessageWithoutIcon): List<MessageDTO>?

    suspend fun getMessagesPage(chatId: Long, messageCount: Int, startIndex: Int): List<MessageDTO>?

    suspend fun saveMessage(chatId: Long, message: MessageWithAuthUser): Boolean

    suspend fun deleteMessage(chatId: Long, messageId: Long, currentUserId: Long): Boolean
}