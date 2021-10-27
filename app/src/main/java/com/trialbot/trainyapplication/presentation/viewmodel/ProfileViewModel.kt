package com.trialbot.trainyapplication.presentation.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.*
import com.trialbot.trainyapplication.data.UserControllerRemote
import com.trialbot.trainyapplication.data.remote.chatServer.ChatApi
import com.trialbot.trainyapplication.domain.EditUserUseCases
import com.trialbot.trainyapplication.presentation.state.ProfileState
import com.trialbot.trainyapplication.utils.default
import kotlinx.coroutines.launch

class ProfileViewModel(
    chatApi: ChatApi,
    sharedPrefs: SharedPreferences,
    private val userId: Long
) : ViewModel() {

    class ProfileViewModelFactory(
        private val chatApi: ChatApi,
        private val sharedPrefs: SharedPreferences,
        private val userId: Long
    ) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(chatApi, sharedPrefs, userId) as T
        }
    }

    private val _state = MutableLiveData<ProfileState>().default(ProfileState.Loading)
    val state: LiveData<ProfileState> = _state

    private val _userIsOnline = MutableLiveData<Boolean?>().default(null)
    val userIsOnline: LiveData<Boolean?> = _userIsOnline

    private val editUserUseCases = EditUserUseCases(chatApi, sharedPrefs)
    private val userControllerRemote = UserControllerRemote(chatApi)

    fun render() {
        viewModelScope.launch {
            _userIsOnline.postValue(userControllerRemote.getUserIsOnline(userId))
        }
    }

    // todo: сделать постоянную проверку доступности пользователя (не очень часто, раз эдак в 20-30 сек)
}