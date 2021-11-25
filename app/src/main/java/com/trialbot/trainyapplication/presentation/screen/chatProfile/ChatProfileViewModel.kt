package com.trialbot.trainyapplication.presentation.screen.chatProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.domain.ChatEditingUseCases
import com.trialbot.trainyapplication.domain.ChatGettingUseCases
import com.trialbot.trainyapplication.domain.LocalDataUseCases
import com.trialbot.trainyapplication.domain.model.ChatDetails
import com.trialbot.trainyapplication.domain.model.UserLocal
import com.trialbot.trainyapplication.domain.model.UserWithoutPassword
import com.trialbot.trainyapplication.domain.utils.logE
import com.trialbot.trainyapplication.presentation.screen.chatProfile.recycler.GetMemberType
import com.trialbot.trainyapplication.presentation.screen.chatProfile.recycler.MemberType
import com.trialbot.trainyapplication.presentation.screen.chatProfile.recycler.MembersAdapterClickListener
import com.trialbot.trainyapplication.utils.default
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ChatProfileViewModel(
    private val chatEditingUseCases: ChatEditingUseCases,
    private val chatGettingUseCases: ChatGettingUseCases,
    private val localDataUseCases: LocalDataUseCases
) : ViewModel(), MembersAdapterClickListener, GetMemberType {

    private val _state = MutableLiveData<ChatProfileState>().default(ChatProfileState.Loading)
    private val _isChatDeleted = MutableLiveData<Boolean?>().default(null)

    val state: LiveData<ChatProfileState> = _state
    val isChatDeleted: LiveData<Boolean?> = _isChatDeleted

    var userId: Long = -1
        private set
    var chatName: String? = null
        private set

    private val _chatMembers = MutableLiveData<List<UserWithoutPassword>?>().default(null)
    val chatMembers: LiveData<List<UserWithoutPassword>?> = _chatMembers

    private val _membersCount = MutableLiveData<Int>().default(0)
    val membersCount: LiveData<Int> = _membersCount

    private var chatId: Long? = null
    private var currentChat: ChatDetails? = null
    private var userType: UserType = UserType.Member
    private var isDialog: Boolean = false
    private var currentUser: UserLocal? = null

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

            this@ChatProfileViewModel._chatMembers.postValue(chatGettingUseCases.getChatMembers(chatId))
            this@ChatProfileViewModel._membersCount.postValue(chatGettingUseCases.getChatMembers(chatId).size)

            this@ChatProfileViewModel.isDialog = currentChat!!.secondDialogMemberId != -1L
            this@ChatProfileViewModel.chatName = getChatName(chatId, isDialog)
            this@ChatProfileViewModel.currentUser = getLocalUser()

            when(this@ChatProfileViewModel.userType) {
                UserType.Admin -> {
                    _state.postValue(ChatProfileState.Admin(
                        chatName = this@ChatProfileViewModel.chatName!!,
                        chatIcon = getChatIcon(isDialog)
                    ))
                }
                UserType.Creator -> {
                    _state.postValue(ChatProfileState.Creator(
                        chatName = this@ChatProfileViewModel.chatName!!,
                        chatIcon = getChatIcon(isDialog),
                        isDialog = isDialog
                    ))
                }
                UserType.Member -> {
                    _state.postValue(ChatProfileState.Member(
                        chatName = this@ChatProfileViewModel.chatName!!,
                        chatIcon = getChatIcon(isDialog)
                    ))
                }
            }
        }
    }

    private suspend fun getChatName(chatId: Long, isDialog: Boolean): String {
        return when {
            isDialog -> chatGettingUseCases.getDialogName(chatId) ?: currentChat!!.name
            currentChat != null -> currentChat!!.name
            else -> {
                logE("Chat is null")
                "Chat"
            }
        }
    }

    private fun getChatIcon(isDialog: Boolean): Int {
        if (isDialog) {
            val myIcon = currentUser?.icon ?: R.drawable.ic_avatar_default
            val icon = if (currentChat?.icon == myIcon) currentChat?.secondIcon ?: R.drawable.ic_avatar_default else currentChat?.icon ?: R.drawable.ic_avatar_default
            return if (icon > 0) icon else R.drawable.ic_avatar_default
        } else {
            return currentChat?.icon ?: R.drawable.ic_default_chat
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

    override fun deleteUserFromChat(userId: Long) {
        viewModelScope.launch {
            if (chatEditingUseCases.removeMember(chatId ?: -1, userId)) {
                var oldMembers = chatMembers.value ?: emptyList()
                if (oldMembers.isNotEmpty()) {
                    oldMembers = chatGettingUseCases.getChatMembers(chatId ?: -1)
                }
                _chatMembers.postValue(oldMembers)
                _membersCount.postValue(oldMembers.size)
            }
        }
    }

    override fun getMemberType(memberId: Long): MemberType {
        return getMemberTypeBlocking(memberId)
    }

    private fun getMemberTypeBlocking(memberId: Long): MemberType = runBlocking {
        if (chatGettingUseCases.checkIsAdmin(chatId ?: -1, memberId) == true)
            return@runBlocking MemberType.ADMIN
        else if (chatGettingUseCases.checkIsCreator(chatId ?: -1, memberId) == true)
            return@runBlocking MemberType.CREATOR
        return@runBlocking MemberType.COMMON_MEMBER
    }
}