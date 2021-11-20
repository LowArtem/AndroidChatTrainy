package com.trialbot.trainyapplication.presentation.screen.chooseChat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trialbot.trainyapplication.domain.ChatEditingUseCases
import com.trialbot.trainyapplication.domain.ChatGettingUseCases
import com.trialbot.trainyapplication.utils.default
import kotlinx.coroutines.launch

class ChooseChatViewModel(
    private val chatGettingUseCases: ChatGettingUseCases,
    private val chatEditingUseCases: ChatEditingUseCases
) : ViewModel() {

    private val _state = MutableLiveData<ChooseChatState>().default(ChooseChatState.Loading)
    val state: LiveData<ChooseChatState> = _state

    private val _isChatAdded = MutableLiveData<Boolean?>().default(null)
    val isChatAdded: LiveData<Boolean?> = _isChatAdded

    fun render(currentUserId: Long) {
        viewModelScope.launch {
            val chats = chatGettingUseCases.getAllChatsByUser(currentUserId)
            if (chats == null) {
                _state.postValue(ChooseChatState.Success(emptyList()))
            } else {
                _state.postValue(ChooseChatState.Success(chats))
            }
        }
    }

    fun addUserToChat(chatId: Long, currentUserId: Long, addedUserId: Long) {
        viewModelScope.launch {
            if (chatGettingUseCases.openChat(chatId) == null) {
                _isChatAdded.postValue(false)
                _state.postValue(ChooseChatState.Error("Chat is null"))
            }

            if (chatGettingUseCases.checkIsAdmin(chatId, currentUserId) == true ||
                chatGettingUseCases.checkIsCreator(chatId, currentUserId) == true) {
                _isChatAdded.postValue(chatEditingUseCases.addMember(chatId, addedUserId))
            } else {
                _isChatAdded.postValue(false)
            }
        }
    }
}