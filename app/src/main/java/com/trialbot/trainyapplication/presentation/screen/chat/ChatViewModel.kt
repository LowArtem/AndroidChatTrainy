package com.trialbot.trainyapplication.presentation.screen.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.trialbot.trainyapplication.domain.ChatEditingUseCases
import com.trialbot.trainyapplication.domain.ChatGettingUseCases
import com.trialbot.trainyapplication.domain.utils.logE

class ChatViewModel(
    private val chatEditingUseCases: ChatEditingUseCases,
    private val chatGettingUseCases: ChatGettingUseCases
) : ViewModel() {

    private val _state = MutableLiveData<ChatState>()
    val state: LiveData<ChatState> = _state

    fun render() {
        try {
            // TODO: реализовать основной функционал

        } catch (e: Exception) {
            logE(e.localizedMessage ?: "Some chat error")
            _state.postValue(ChatState.Error("Chat loading error"))
        }
    }
}