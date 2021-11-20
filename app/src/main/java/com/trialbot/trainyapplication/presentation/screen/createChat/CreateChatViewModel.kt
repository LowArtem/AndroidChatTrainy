package com.trialbot.trainyapplication.presentation.screen.createChat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trialbot.trainyapplication.domain.ChatEditingUseCases
import com.trialbot.trainyapplication.domain.model.ChatCreating
import com.trialbot.trainyapplication.utils.default
import kotlinx.coroutines.launch

class CreateChatViewModel(private val chatEditingUseCases: ChatEditingUseCases) : ViewModel() {
    private var currentUserId: Long? = null

    private val _isChatSuccessfullyCreated =  MutableLiveData<Boolean?>().default(null)
    val isChatSuccessfullyCreated: LiveData<Boolean?> = _isChatSuccessfullyCreated

    var chatCreatedId: Long? = null
        private set

    fun init(currentUserId: Long) {
        this.currentUserId = currentUserId
    }

    fun createChat(name: String, icon: Int = -1, about: String? = null) {
        if (currentUserId != null) {
            viewModelScope.launch {
                val createdChat = chatEditingUseCases.createChat(
                    ChatCreating(
                        name = name,
                        about = about ?: "",
                        icon = icon,
                        creatorId = currentUserId!!
                    )
                )
                if (createdChat != null) {
                    _isChatSuccessfullyCreated.postValue(true)
                    chatCreatedId = createdChat
                } else {
                    _isChatSuccessfullyCreated.postValue(false)
                    chatCreatedId = null
                }
            }
        }
    }
}