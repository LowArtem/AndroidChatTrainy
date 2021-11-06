package com.trialbot.trainyapplication.presentation.screen.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trialbot.trainyapplication.domain.ChatEditingUseCases
import com.trialbot.trainyapplication.domain.ChatGettingUseCases
import com.trialbot.trainyapplication.domain.utils.logE
import com.trialbot.trainyapplication.utils.default
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatEditingUseCases: ChatEditingUseCases,
    private val chatGettingUseCases: ChatGettingUseCases
) : ViewModel() {

    private val _state = MutableLiveData<ChatState>().default(ChatState.Loading)
    val state: LiveData<ChatState> = _state

    fun render(userId: Long) {
        try {
            viewModelScope.launch {
                val chats = chatGettingUseCases.getAllChatsByUser(userId)

                if (chats.isNullOrEmpty()) {
                    _state.postValue(ChatState.Empty)
                } else {
                    _state.postValue(ChatState.Success(chats))
                }
            }
        } catch (e: Exception) {
            logE(e.localizedMessage ?: "Some chat error")
            _state.postValue(ChatState.Error("Chat loading error"))
        }
    }
}