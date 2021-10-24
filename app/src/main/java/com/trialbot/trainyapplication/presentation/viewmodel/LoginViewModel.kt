package com.trialbot.trainyapplication.presentation.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.data.AuthenticationControllerLocal
import com.trialbot.trainyapplication.data.AuthenticationControllerRemote
import com.trialbot.trainyapplication.data.model.UserWithoutPassword
import com.trialbot.trainyapplication.data.remote.chatServer.ChatApi
import com.trialbot.trainyapplication.domain.AuthUseCases
import com.trialbot.trainyapplication.domain.LoginStatusUseCases
import com.trialbot.trainyapplication.domain.StartStopRemoteActions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(
    chatApi: ChatApi,
    sharedPrefs: SharedPreferences
) : ViewModel() {

    class LoginViewModelFactory(
        private val chatApi: ChatApi,
        private val sharedPrefs: SharedPreferences
    ) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LoginViewModel(chatApi, sharedPrefs) as T
        }
    }



    private val loginStatus = LoginStatusUseCases(sharedPrefs)
    private val authUseCases = AuthUseCases(sharedPrefs, chatApi)
    private val startStopRemoteActions = StartStopRemoteActions(
        AuthenticationControllerRemote(chatApi),
        AuthenticationControllerLocal(sharedPrefs)
    )


    var username: String? = null
        private set

    var avatarId: Int = -1
        private set

    private var isLoginSuccessfulMutable: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoginSuccessful: LiveData<Boolean> = isLoginSuccessfulMutable


    fun login(usernameEntered: String, passwordEntered: String) {
        viewModelScope.launch {
            val user = authUseCases.login(usernameEntered, passwordEntered)
            setUserData(user)
        }
    }

    fun register(usernameEntered: String, passwordEntered: String) {
        viewModelScope.launch {
            val user = authUseCases.register(usernameEntered, passwordEntered)
            setUserData(user)
        }
    }

    private fun setUserData(user: UserWithoutPassword?) {
        if (user != null) {
            // TODO: добавить сюда изменение других параметров (isLoginSuccessfulMutable дложно быть в конце)
            avatarId = user.icon
            username = user.username
            isLoginSuccessfulMutable.value = true
        } else {
            avatarId = -1
            username = null
            isLoginSuccessfulMutable.value = false
        }
    }

    fun setUserOnline() {
        // Обратить внимание на возможные утечки памяти
        // этот scope не закрывается после закрытия ViewModel
        CoroutineScope(Dispatchers.Main).launch {
            try {
                startStopRemoteActions.appStarted(avatarId)
            } catch (e: Exception) {
                Log.e(MyApp.ERROR_LOG_TAG, "LoginViewModel.setUserOnline -> ${e.localizedMessage}")
                return@launch
            }
        }
    }

    fun saveUserLoginStatus() {
        loginStatus.saveLoginStatus(isLoginSuccessful.value ?: false)
    }

    fun getUserLoginStatus() {
        if (loginStatus.getLoginStatus()) {
            getLocalUserData()
            isLoginSuccessfulMutable.value = true
        }
        else
            isLoginSuccessfulMutable.value = false
    }

    private fun getLocalUserData() {
        val userLocal = authUseCases.localDataUseCases.getLocalData()
        if (userLocal != null) {
            username = userLocal.username
        }
    }
}