package com.trialbot.trainyapplication.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.trialbot.trainyapplication.data.MessageControllerRemote
import com.trialbot.trainyapplication.data.model.MessageDTO
import com.trialbot.trainyapplication.data.model.MessageWithAuthUser
import com.trialbot.trainyapplication.data.remote.chatServer.ChatApi

class MessageUseCases(
    chatApi: ChatApi,
) {

    private val messageControllerRemote = MessageControllerRemote(chatApi)

    private val _messages = MutableLiveData<List<MessageDTO>?>()
    val messages: LiveData<List<MessageDTO>?> = _messages


    suspend fun sendMessage(message: MessageWithAuthUser): Boolean {
        val isSuccessful = messageControllerRemote.saveMessage(message)
        updateMessages()

        return isSuccessful
    }

    suspend fun updateMessages() {
        val gotMessages = messageControllerRemote.getAllMessages()

        when {
            gotMessages == null -> {
                _messages.postValue(emptyList())
            }
            gotMessages.last() != _messages.value?.last() -> {
                _messages.postValue(gotMessages)
            }
            else -> {

            }
        }
    }
}