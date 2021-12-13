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
import com.trialbot.trainyapplication.presentation.screen.chatProfile.recycler.MembersAdapterClickListener
import com.trialbot.trainyapplication.utils.BooleanState
import com.trialbot.trainyapplication.utils.MutableBooleanState
import com.trialbot.trainyapplication.utils.default
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ChatProfileViewModel(
    private val chatEditingUseCases: ChatEditingUseCases,
    private val chatGettingUseCases: ChatGettingUseCases,
    private val localDataUseCases: LocalDataUseCases
) : ViewModel(), MembersAdapterClickListener, GetMemberType {

    private val _state = MutableLiveData<ChatProfileState>().default(ChatProfileState.Loading)
    private val _selectedUserId = MutableLiveData<Long?>().default(null)

    private val _isChatDeleted = MutableBooleanState().default(null)
    private val _isAdminAdded = MutableBooleanState().default(null)
    private val _isAdminDeleted = MutableBooleanState().default(null)
    private val _isMemberDeleted = MutableBooleanState().default(null)
    private val _isChatLeft = MutableBooleanState().default(null)


    val state: LiveData<ChatProfileState> = _state
    val selectedUserId: LiveData<Long?> = _selectedUserId

    val isChatDeleted: BooleanState = _isChatDeleted
    val isAdminAdded: BooleanState = _isAdminAdded
    val isAdminDeleted: BooleanState = _isAdminDeleted
    val isMemberDeleted: BooleanState = _isMemberDeleted
    val isChatLeft: BooleanState = _isChatLeft


    var currentUserId: Long = -1
        private set
    var chatName: String? = null
        private set
    var chatAbout: String = ""
        private set

    private val _chatMembers = MutableLiveData<List<UserWithoutPassword>?>().default(null)
    val chatMembers: LiveData<List<UserWithoutPassword>?> = _chatMembers

    private val _membersCount = MutableLiveData<Int>().default(0)
    val membersCount: LiveData<Int> = _membersCount

    val isAboutChanged = MutableBooleanState().default(null)

    private var chatId: Long? = null
    private var currentChat: ChatDetails? = null
    private var userType: UserType = UserType.Member
    private var isDialog: Boolean = false
    private var currentUser: UserLocal? = null

    private var adminIds: MutableList<Long> = mutableListOf()
    private var creatorIds: MutableList<Long>? = null

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
            this@ChatProfileViewModel.currentUserId = userId
            this@ChatProfileViewModel.userType = UserType.fromString(userType)

            this@ChatProfileViewModel._chatMembers.postValue(chatGettingUseCases.getChatMembers(chatId))
            this@ChatProfileViewModel._membersCount.postValue(chatGettingUseCases.getChatMembers(chatId).size)


            this@ChatProfileViewModel.isDialog = currentChat!!.secondDialogMemberId != -1L

            if (!this@ChatProfileViewModel.isDialog) {
                this@ChatProfileViewModel.chatAbout = currentChat!!.about
                isAboutChanged.postValue(true)
            }

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
            isDialog -> chatGettingUseCases.getDialogNameSuspend(chatId) ?: currentChat!!.name
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

    fun editAbout(text: String) {
        viewModelScope.launch {
            if (userType == UserType.Admin || userType == UserType.Creator) {
                val isUpdated = chatEditingUseCases.updateChatAbout(chatId ?: -1, text)
                if (isUpdated) {
                    this@ChatProfileViewModel.chatAbout = text
                } else {
                    isAboutChanged.postValue(true)
                }
            }
        }
    }

    fun leaveChat() {
        viewModelScope.launch {
            if (userType == UserType.Admin) {
                chatEditingUseCases.removeAdmin(chatId ?: -1, currentUserId)
            }
            _isChatLeft.postValue(chatEditingUseCases.removeMember(chatId ?: -1, currentUserId))
        }
    }

    fun deleteChat() {
        viewModelScope.launch {
            _isChatDeleted.postValue(chatEditingUseCases.deleteChat(chatId ?: -1, currentUserId))
        }
    }

    fun addAdmin() {
        viewModelScope.launch {
            val result = chatEditingUseCases.addAdmin(chatId ?: -1, selectedUserId.value ?: -1)
            if (result) {
                adminIds.add(selectedUserId.value!!)
                unselectMember()
            }
            _isAdminAdded.postValue(result)
        }
    }

    fun deleteAdmin() {
        viewModelScope.launch {
            val result = chatEditingUseCases.removeAdmin(chatId ?: -1, selectedUserId.value ?: -1)
            if (result) {
                adminIds.remove(selectedUserId.value!!)
                unselectMember()
            }
            _isAdminDeleted.postValue(result)
        }
    }

    fun deleteMember() {
        viewModelScope.launch {
            if (chatEditingUseCases.removeMember(chatId ?: -1, selectedUserId.value ?: -1)) {
                var oldMembers = chatMembers.value ?: emptyList()
                if (oldMembers.isNotEmpty()) {
                    oldMembers = chatGettingUseCases.getChatMembers(chatId ?: -1)
                }
                _chatMembers.postValue(oldMembers)
                _membersCount.postValue(oldMembers.size)
                unselectMember()
                _isMemberDeleted.postValue(true)
            }
            else _isMemberDeleted.postValue(false)
        }
    }

    fun getLocalUser(): UserLocal? {
        return localDataUseCases.getLocalData()
    }

    override fun memberSelected(selectedUserId: Long) {
        _selectedUserId.postValue(selectedUserId)
    }

    fun unselectMember() {
        _selectedUserId.postValue(null)
    }

    override fun getMemberType(memberId: Long): UserType {
        return UserType.fromString(getUserType(chatId ?: -1, memberId))
    }

    private fun getUserType(chatId: Long, userId: Long = currentUserId): String = runBlocking(Dispatchers.IO) {
        if (adminIds.isEmpty()) adminIds = chatGettingUseCases.getAdminIds(chatId).toMutableList()
        if (creatorIds.isNullOrEmpty()) creatorIds = mutableListOf(chatGettingUseCases.openChat(chatId)?.creatorId ?:
                return@runBlocking UserType.Member.toString())

        if (isDialog) creatorIds?.add(currentChat?.secondDialogMemberId!!)

        return@runBlocking when {
            creatorIds?.contains(userId) ?: false -> {
                UserType.Creator.toString()
            }
            adminIds.contains(userId) -> {
                UserType.Admin.toString()
            }
            else -> {
                UserType.Member.toString()
            }
        }
    }
}