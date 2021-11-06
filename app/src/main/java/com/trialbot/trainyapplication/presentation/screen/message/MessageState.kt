package com.trialbot.trainyapplication.presentation.screen.message

import com.trialbot.trainyapplication.domain.model.MessageDTO

sealed class MessageState {
    object Loading : MessageState()
    object Empty : MessageState()
    data class Success(val messages: List<MessageDTO>) : MessageState()
    data class Error(val errorText: String) : MessageState()
}
