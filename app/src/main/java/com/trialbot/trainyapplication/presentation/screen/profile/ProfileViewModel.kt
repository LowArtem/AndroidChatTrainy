package com.trialbot.trainyapplication.presentation.screen.profile

import androidx.lifecycle.*
import com.trialbot.trainyapplication.domain.*
import com.trialbot.trainyapplication.domain.model.*
import com.trialbot.trainyapplication.domain.utils.logE
import com.trialbot.trainyapplication.presentation.drawable.AvatarController
import com.trialbot.trainyapplication.utils.BooleanState
import com.trialbot.trainyapplication.utils.MutableBooleanState
import com.trialbot.trainyapplication.utils.default
import kotlinx.coroutines.launch
import java.util.*

class ProfileViewModel(
    private val editUserUseCases: EditUserUseCases,
    private val loginStatus: LoginStatusUseCases,
    private val localDataUseCases: LocalDataUseCases,
    private val userStatusDataUseCases: UserStatusDataUseCases,
    private val startStopRemoteActions: StartStopRemoteActions,
    private val chatEditingUseCases: ChatEditingUseCases,
    private val chatGettingUseCases: ChatGettingUseCases
) : ViewModel() {


    private val _state = MutableLiveData<ProfileState>().default(ProfileState.Loading)
    val state: LiveData<ProfileState> = _state

    private val _isDialogSuccessfullyCreated = MutableBooleanState().default(null)
    val isDialogSuccessfullyCreated: BooleanState = _isDialogSuccessfullyCreated

    var chatCreatedId: Long? = null
        private set

    var createdChat: ChatDetails? = null
        private set

    var user: User? = null
        private set

    fun render(viewState: String, userId: Long, username: String, userIcon: Int) {
        if (username == "Username") {
            logE("username is null")
            _state.postValue(ProfileState.Error("Cannot detect this user"))
            return
        }

        viewModelScope.launch {
            val userLastDate: Date? = userStatusDataUseCases.getUserLastDate(userId)
            if (userLastDate == null) {
                logE("user's last date is null")
                _state.postValue(ProfileState.Error("Cannot detect this user"))
                return@launch
            }

            val userIsOnline: Boolean = userStatusDataUseCases.getUserIsOnline(userId)

            when(viewState) {
                "guest" -> {
                    val userGuest = UserWithoutPassword(userId, userIcon, username, userIsOnline, userLastDate)
                    this@ProfileViewModel.user = userGuest
                    _state.postValue(ProfileState.ReadOnly(userGuest))
                }
                "owner" -> {
                    val userLocal: UserLocal? = localDataUseCases.getLocalData()
                    if (userLocal == null) {
                        logE("user is null")
                        _state.postValue(ProfileState.Error("Cannot define this user"))
                        return@launch
                    }
                    val user = UserFull(userId, userIcon, username, userLocal.password, true, userLastDate)

                    this@ProfileViewModel.user = user
                    _state.postValue(ProfileState.ReadWrite(user))
                }
                else -> {
                    this@ProfileViewModel.user = null
                    logE("unknown viewState (guest/owner)")
                    _state.postValue(ProfileState.Error("Application error"))
                    return@launch
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            startStopRemoteActions.appClosed()
        }
        loginStatus.saveLoginStatus(false)
    }

    fun sendMessageToUser(currentUserId: Long, selectedUserId: Long) {
        if (currentUserId > 0 && selectedUserId > 0) {
            viewModelScope.launch {
                val createdDialogId = chatEditingUseCases.createDialog(currentUserId, selectedUserId)
                if (createdDialogId != null) {
                    createdChat = chatGettingUseCases.openChat(createdDialogId)
                    _isDialogSuccessfullyCreated.postValue(true)
                    chatCreatedId = createdDialogId
                } else {
                    _isDialogSuccessfullyCreated.postValue(false)
                    chatCreatedId = null
                }
            }
        }
    }

    fun confirmNewPassword(newPassword: String, handler: (Boolean) -> Unit) {
        viewModelScope.launch {
            val changePasswordResult = editUserUseCases.changePassword(user as UserFull, newPassword)
            if (changePasswordResult) {
                user = (user as UserFull).copy(password = newPassword)
            }
            handler(changePasswordResult)
        }
    }

    fun editAvatar() {
        _state.postValue(ProfileState.AvatarChangingOpened(AvatarController.getAvatars()))
    }

    fun saveAvatar(avatarId: Int) {
        if (user is UserFull) {
            viewModelScope.launch {
                if (editUserUseCases.changeIcon(user as UserFull, avatarId)) {
                    _state.postValue(ProfileState.AvatarChangingClosing(avatarId))
                    user = UserFull(
                        (user as UserFull).id,
                        avatarId,
                        (user as UserFull).username,
                        (user as UserFull).password,
                        (user as UserFull).isOnline,
                        (user as UserFull).lastDate
                    )
                } else {
                    _state.postValue(ProfileState.AvatarChangingClosing((user as UserFull).icon))
                }
            }
        } else {
            logE("wrong user type or null")
            _state.postValue(ProfileState.Error("Cannot define this user"))
        }
    }

    fun cancelChangeAvatar() {
        _state.postValue(ProfileState.AvatarChangingClosing((user as UserFull).icon))
    }

    fun checkCurrentPassword(password: String): Boolean {
        if (user is UserFull) {
            return (user as UserFull).password == password
        }
        return false
    }

    fun getDialogName(username: String): String? {
        return if (createdChat != null) {
            chatGettingUseCases.getDialogNameInverted(username, createdChat!!.name)
        } else {
            null
        }
    }

    fun getDialogIcon(username: String): Int? {
        return if (createdChat != null) {
            chatGettingUseCases.getDialogIconInverted(username, createdChat!!)
        } else {
            null
        }
    }

    // Нужно очищать результат создания диалога, иначе, если внутри чата нажать назад, то будет опять перебрасывать в чат
    fun cleanDialogCreatedResult() {
        _isDialogSuccessfullyCreated.postValue(null)
    }
}