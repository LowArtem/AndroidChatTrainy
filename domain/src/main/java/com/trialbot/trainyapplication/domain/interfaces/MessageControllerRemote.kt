package com.trialbot.trainyapplication.domain.interfaces

import com.trialbot.trainyapplication.domain.model.MessageDTO
import com.trialbot.trainyapplication.domain.model.MessageWithAuthUser
import com.trialbot.trainyapplication.domain.model.UserMessageWithoutIcon

interface MessageControllerRemote {

    suspend fun getAllMessages(): List<MessageDTO>?

    suspend fun getMessagesByUser(user: UserMessageWithoutIcon): List<MessageDTO>?

    suspend fun saveMessage(message: MessageWithAuthUser): Boolean
}