package com.trialbot.trainyapplication.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.trialbot.trainyapplication.data.MessageControllerRemote
import com.trialbot.trainyapplication.data.model.MessageDTO
import com.trialbot.trainyapplication.data.model.MessageWithAuthUser
import com.trialbot.trainyapplication.data.remote.chatServer.ChatApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MessageUseCases(
    chatApi: ChatApi,
    private val viewModelScope: CoroutineScope
) {

    private val messageControllerRemote = MessageControllerRemote(chatApi)

    private val _messages = MutableLiveData<List<MessageDTO>>()
    val messages: LiveData<List<MessageDTO>> = _messages

    init {
        this.updateMessages()
    }

    fun sendMessage(message: MessageWithAuthUser): Boolean {
        var isSuccessful = false
        viewModelScope.launch {
            isSuccessful = messageControllerRemote.saveMessage(message)
            updateMessages()
        }
        return isSuccessful
    }

    fun updateMessages() {
        viewModelScope.launch {
            val gotMessages = messageControllerRemote.getAllMessages()
            _messages.value = gotMessages ?: emptyList()
        }
    }
}