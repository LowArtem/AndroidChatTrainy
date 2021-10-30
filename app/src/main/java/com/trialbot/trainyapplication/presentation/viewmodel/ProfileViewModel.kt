package com.trialbot.trainyapplication.presentation.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.data.UserControllerRemote
import com.trialbot.trainyapplication.data.model.User
import com.trialbot.trainyapplication.data.model.UserFull
import com.trialbot.trainyapplication.data.model.UserLocal
import com.trialbot.trainyapplication.data.model.UserWithoutPassword
import com.trialbot.trainyapplication.data.remote.chatServer.ChatApi
import com.trialbot.trainyapplication.domain.EditUserUseCases
import com.trialbot.trainyapplication.domain.GetAvatarsUseCases
import com.trialbot.trainyapplication.domain.LocalDataUseCases
import com.trialbot.trainyapplication.domain.LoginStatusUseCases
import com.trialbot.trainyapplication.presentation.state.ProfileState
import com.trialbot.trainyapplication.utils.default
import kotlinx.coroutines.launch
import java.util.*

class ProfileViewModel(
    chatApi: ChatApi,
    sharedPrefs: SharedPreferences,
) : ViewModel() {

    class ProfileViewModelFactory(
        private val chatApi: ChatApi,
        private val sharedPrefs: SharedPreferences,
    ) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(chatApi, sharedPrefs) as T
        }
    }

    private val _state = MutableLiveData<ProfileState>().default(ProfileState.Loading)
    val state: LiveData<ProfileState> = _state

    private val editUserUseCases = EditUserUseCases(chatApi, sharedPrefs)
    private val userControllerRemote = UserControllerRemote(chatApi)
    private val loginStatus = LoginStatusUseCases(sharedPrefs)
    private val localDataUseCases = LocalDataUseCases(sharedPrefs)

    private var _user: User? = null
    val user: User? = _user

    fun render(viewState: String, userId: Long, username: String, userIcon: Int) {
        if (username == "Username") {
            Log.e(MyApp.ERROR_LOG_TAG, "ProfileViewModel.render() -> username is null")
            _state.postValue(ProfileState.Error("Cannot detect this user"))
            return
        }

        viewModelScope.launch {
            val userLastDate: Date? = userControllerRemote.getUserLastDate(userId)
            if (userLastDate == null) {
                Log.e(MyApp.ERROR_LOG_TAG, "ProfileViewModel.render() -> user's last date is null")
                _state.postValue(ProfileState.Error("Cannot detect this user"))
                return@launch
            }

            val userIsOnline: Boolean = userControllerRemote.getUserIsOnline(userId)

            when(viewState) {
                "guest" -> {
                    val userGuest = UserWithoutPassword(userId, userIcon, username, userIsOnline, userLastDate)
                    this@ProfileViewModel._user = userGuest
                    _state.postValue(ProfileState.ReadOnly(userGuest))
                }
                "owner" -> {
                    val userLocal: UserLocal? = localDataUseCases.getLocalData()
                    if (userLocal == null) {
                        Log.e(MyApp.ERROR_LOG_TAG, "ProfileViewModel.render() -> user is null")
                        _state.postValue(ProfileState.Error("Cannot define this user"))
                        return@launch
                    }
                    val user = UserFull(userId, userIcon, username, userLocal.password, true, userLastDate)

                    this@ProfileViewModel._user = user
                    _state.postValue(ProfileState.ReadWrite(user))
                }
                else -> {
                    this@ProfileViewModel._user = null
                    Log.e(MyApp.ERROR_LOG_TAG, "ProfileViewModel.render() -> unknown viewState (guest/owner)")
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
        val getAvatars = GetAvatarsUseCases()
        _state.postValue(ProfileState.AvatarChangingOpened(getAvatars.getAvatars()))
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
            Log.e(MyApp.ERROR_LOG_TAG, "ProfileViewModel.saveAvatar() -> wrong user type or null")
            _state.postValue(ProfileState.Error("Cannot define this user"))
        }
    }

    fun cancelChangeAvatar() {
        _state.postValue(ProfileState.AvatarChangingClosing((_user as UserFull).icon))
    }
}