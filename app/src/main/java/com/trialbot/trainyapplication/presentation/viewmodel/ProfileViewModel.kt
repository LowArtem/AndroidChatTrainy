package com.trialbot.trainyapplication.presentation.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.trialbot.trainyapplication.data.remote.chatServer.ChatApi
import com.trialbot.trainyapplication.domain.EditUserUseCases
import com.trialbot.trainyapplication.presentation.state.ProfileState
import com.trialbot.trainyapplication.utils.default

class ProfileViewModel(
    chatApi: ChatApi,
    sharedPrefs: SharedPreferences
) : ViewModel() {

    class ProfileViewModelFactory(
        private val chatApi: ChatApi,
        private val sharedPrefs: SharedPreferences
    ) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(chatApi, sharedPrefs) as T
        }
    }

    private val _state = MutableLiveData<ProfileState>().default(ProfileState.Loading)
    val state: LiveData<ProfileState> = _state

    private val editUserUseCases = EditUserUseCases(chatApi, sharedPrefs)

    fun render() {

    }
}