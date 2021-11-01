package com.trialbot.trainyapplication.presentation.screen.profile

import androidx.lifecycle.*
import com.trialbot.trainyapplication.domain.EditUserUseCases
import com.trialbot.trainyapplication.domain.LocalDataUseCases
import com.trialbot.trainyapplication.domain.LoginStatusUseCases
import com.trialbot.trainyapplication.domain.UserStatusDataUseCases
import com.trialbot.trainyapplication.domain.model.User
import com.trialbot.trainyapplication.domain.model.UserFull
import com.trialbot.trainyapplication.domain.model.UserLocal
import com.trialbot.trainyapplication.domain.model.UserWithoutPassword
import com.trialbot.trainyapplication.domain.utils.logE
import com.trialbot.trainyapplication.presentation.drawable.AvatarController
import com.trialbot.trainyapplication.presentation.state.ProfileState
import com.trialbot.trainyapplication.utils.default
import kotlinx.coroutines.launch
import java.util.*

class ProfileViewModel(
    private val editUserUseCases: EditUserUseCases,
    private val loginStatus: LoginStatusUseCases,
    private val localDataUseCases: LocalDataUseCases,
    private val userStatusDataUseCases: UserStatusDataUseCases
) : ViewModel() {


    private val _state = MutableLiveData<ProfileState>().default(ProfileState.Loading)
    val state: LiveData<ProfileState> = _state

    private var _user: User? = null
    val user: User? = _user

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
                    this@ProfileViewModel._user = userGuest
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

                    this@ProfileViewModel._user = user
                    _state.postValue(ProfileState.ReadWrite(user))
                }
                else -> {
                    this@ProfileViewModel._user = null
                    logE("unknown viewState (guest/owner)")
                    _state.postValue(ProfileState.Error("Application error"))
                    return@launch
                }
            }
        }
    }

    fun logout() {
        loginStatus.saveLoginStatus(false)
    }

    fun addUserToChat() {

    }

    fun createChat() {

    }

    fun editPassword() {

    }

    fun editAvatar() {
        _state.postValue(ProfileState.AvatarChangingOpened(AvatarController.getAvatars()))
    }

    fun sendMessageToUser() {

    }

    fun saveAvatar(avatarId: Int) {
        if (_user is UserFull) {
            viewModelScope.launch {
                if (editUserUseCases.changeIcon(_user as UserFull, avatarId)) {
                    _state.postValue(ProfileState.AvatarChangingClosing(avatarId))
                    _user = UserFull(
                        (_user as UserFull).id,
                        avatarId,
                        (_user as UserFull).username,
                        (_user as UserFull).password,
                        (_user as UserFull).isOnline,
                        (_user as UserFull).lastDate
                    )
                } else {
                    _state.postValue(ProfileState.AvatarChangingClosing((_user as UserFull).icon))
                }
            }
        } else {
            logE("wrong user type or null")
            _state.postValue(ProfileState.Error("Cannot define this user"))
        }
    }

    fun cancelChangeAvatar() {
        _state.postValue(ProfileState.AvatarChangingClosing((_user as UserFull).icon))
    }
}