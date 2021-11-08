package com.trialbot.trainyapplication.presentation.screen.chatProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trialbot.trainyapplication.domain.ChatEditingUseCases
import com.trialbot.trainyapplication.domain.ChatGettingUseCases
import com.trialbot.trainyapplication.domain.LocalDataUseCases
import com.trialbot.trainyapplication.domain.model.ChatDetails
import com.trialbot.trainyapplication.domain.model.UserLocal
import com.trialbot.trainyapplication.domain.model.UserWithoutPassword
import com.trialbot.trainyapplication.domain.utils.logE
import com.trialbot.trainyapplication.utils.default
import kotlinx.coroutines.launch

class ChatProfileViewModel(
    private val chatEditingUseCases: ChatEditingUseCases,
    private val chatGettingUseCases: ChatGettingUseCases,
    private val localDataUseCases: LocalDataUseCases
) : ViewModel() {

    private val _state = MutableLiveData<ChatProfileState>().default(ChatProfileState.Loading)
    private val _isChatDeleted = MutableLiveData<Boolean?>().default(null)

    val state: LiveData<ChatProfileState> = _state
    val isChatDeleted: LiveData<Boolean?> = _isChatDeleted

    private var chatId: Long? = null
    private var currentChat: ChatDetails? = null
    var userId: Long = -1
        private set
    private var userType: UserType = UserType.Member
    private var chatMembers: List<UserWithoutPassword>? = null

    fun render(userType: String, userId: Long, chatId: Long) {
        viewModelScope.launch {
            currentChat = chatGettingUseCases.openChat(chatId)
            if (currentChat == null) {
                logE("Chat is null")
                _state.postValue(ChatProfileState.Error("Chat is null"))
                return@launch
            }
            if (userId == -1L) {
                logE("UserId is null (-1)")
                _state.postValue(ChatProfileState.Error("User ID is null"))
                return@launch
            }
            this@ChatProfileViewModel.chatId = chatId
            this@ChatProfileViewModel.userId = userId
            this@ChatProfileViewModel.userType = UserType.fromString(userType)
            this@ChatProfileViewModel.chatMembers = chatGettingUseCases.getChatMembers(chatId)

            when(this@ChatProfileViewModel.userType) {
                UserType.Admin -> {
                    _state.postValue(ChatProfileState.Admin(
                        chatName = currentChat!!.name,
                        chatIcon = currentChat!!.icon,
                        chatMembersCount = this@ChatProfileViewModel.chatMembers!!.count()
                    ))
                }
                UserType.Creator -> {
                    _state.postValue(ChatProfileState.Creator(
                        chatName = currentChat!!.name,
                        chatIcon = currentChat!!.icon,
                        chatMembersCount = this@ChatProfileViewModel.chatMembers!!.count()
                    ))
                }
                UserType.Member -> {
                    _state.postValue(ChatProfileState.Member(
                        chatName = currentChat!!.name,
                        chatIcon = currentChat!!.icon,
                        chatMembersCount = this@ChatProfileViewModel.chatMembers!!.count()
                    ))
                }
            }
        }
    }

    fun deleteChat() {
        viewModelScope.launch {
            _isChatDeleted.postValue(chatEditingUseCases.deleteChat(chatId ?: -1, userId))
        }
    }

    fun getLocalUser(): UserLocal? {
        return localDataUseCases.getLocalData()
    }
}