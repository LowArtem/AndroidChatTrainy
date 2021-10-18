package com.trialbot.trainyapplication.presentation.state

import com.trialbot.trainyapplication.data.model.MessageDTO

sealed class MessageState {
    object Loading : MessageState()
    object Empty : MessageState()
    data class Success(val messages: List<MessageDTO>) : MessageState()
    data class Error(val errorText: String) : MessageState()
}
