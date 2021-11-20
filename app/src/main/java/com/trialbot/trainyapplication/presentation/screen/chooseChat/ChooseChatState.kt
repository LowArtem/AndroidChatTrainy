package com.trialbot.trainyapplication.presentation.screen.chooseChat

import com.trialbot.trainyapplication.domain.model.ChatInfo

sealed class ChooseChatState {
    object Loading : ChooseChatState()
    data class Success(val chats: List<ChatInfo>) : ChooseChatState()
    data class Error(val errorMessage: String) : ChooseChatState()
}