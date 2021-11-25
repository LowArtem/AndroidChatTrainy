package com.trialbot.trainyapplication.presentation.screen.createChat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trialbot.trainyapplication.domain.ChatEditingUseCases
import com.trialbot.trainyapplication.domain.FindUsersUseCases
import com.trialbot.trainyapplication.domain.model.ChatCreating
import com.trialbot.trainyapplication.domain.model.UserWithoutPassword
import com.trialbot.trainyapplication.presentation.screen.createChat.recycler.UserSearchAdapterClickAction
import com.trialbot.trainyapplication.utils.default
import kotlinx.coroutines.launch

class CreateChatViewModel(
    private val chatEditingUseCases: ChatEditingUseCases,
    private val findUsersUseCases: FindUsersUseCases
) : ViewModel(),
    UserSearchAdapterClickAction {
    private var currentUserId: Long? = null

    private val _isChatSuccessfullyCreated =  MutableLiveData<Boolean?>().default(null)
    val isChatSuccessfullyCreated: LiveData<Boolean?> = _isChatSuccessfullyCreated

    var chatCreatedId: Long? = null
        private set

    private val _foundedUsers = MutableLiveData<List<UserWithoutPassword>>().default(emptyList())
    val foundedUsers: LiveData<List<UserWithoutPassword>> = _foundedUsers

    fun init(currentUserId: Long) {
        this.currentUserId = currentUserId
    }

    fun createChat(name: String, icon: Int = -1, about: String? = null) {
        if (currentUserId != null) {
            viewModelScope.launch {
                val createdChatId = chatEditingUseCases.createChat(
                    ChatCreating(
                        name = name,
                        about = about ?: "",
                        icon = icon,
                        creatorId = currentUserId!!
                    )
                )
                if (createdChatId != null) {
                    _isChatSuccessfullyCreated.postValue(true)
                    chatCreatedId = createdChatId
                } else {
                    _isChatSuccessfullyCreated.postValue(false)
                    chatCreatedId = null
                }
            }
        }
    }

    fun searchUsers(query: String) {
        viewModelScope.launch {
            _foundedUsers.postValue(findUsersUseCases.findUsersByUsername(query))
        }
    }

    override fun createDialog(selectedUserId: Long) {
        if (currentUserId != null && currentUserId!! > 0 && selectedUserId > 0) {
            viewModelScope.launch {
                val createdDialogId = chatEditingUseCases.createDialog(currentUserId!!, selectedUserId)
                if (createdDialogId != null) {
                    _isChatSuccessfullyCreated.postValue(true)
                    chatCreatedId = createdDialogId
                } else {
                    _isChatSuccessfullyCreated.postValue(false)
                    chatCreatedId = null
                }
            }
        }
    }
}