package com.trialbot.trainyapplication.presentation.screen.chat

import com.trialbot.trainyapplication.domain.model.ChatInfo

sealed class ChatState {
    object Loading : ChatState()
    object Empty : ChatState()
    data class Success(val chats: List<ChatInfo>) : ChatState()
    data class Error(val message: String) : ChatState()
}