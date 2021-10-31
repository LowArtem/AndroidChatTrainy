package com.trialbot.trainyapplication.presentation.viewmodel

import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.*
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.data.model.UserWithoutPassword
import com.trialbot.trainyapplication.data.remote.chatServer.ChatApi
import com.trialbot.trainyapplication.domain.AuthUseCases
import com.trialbot.trainyapplication.domain.LoginStatusUseCases
import com.trialbot.trainyapplication.presentation.state.LoginState
import com.trialbot.trainyapplication.utils.default
import com.trialbot.trainyapplication.utils.isInternetAvailable
import com.trialbot.trainyapplication.utils.isServerAvailable
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

    private val _state = MutableLiveData<LoginState>().default(LoginState.Loading)
    val state: LiveData<LoginState> = _state


    private val loginStatus = LoginStatusUseCases(sharedPrefs)
    private val authUseCases = AuthUseCases(sharedPrefs, chatApi)


    var username: String? = null
        private set

    var avatarId: Int = -1
        private set

    private var isLoginSuccessful: Boolean = false


    fun render(connectivityManager: ConnectivityManager) = viewModelScope.launch {
        var isServerAvailable = false

        if (isInternetAvailable(connectivityManager)) {
            isServerAvailable = isServerAvailable(connectivityManager)
        }
        else {
            Log.e(MyApp.ERROR_LOG_TAG, "LoginViewModel.render() -> internet unavailable")
            _state.postValue(LoginState.Error("Internet connection is unavailable"))
        }

        if (isServerAvailable) {
            if (getUserLoginStatus()) {
                isLoginSuccessful = true
                _state.postValue(LoginState.Success(username!!, avatarId))
            } else {
                isLoginSuccessful = false
                _state.postValue(LoginState.UserNotFound("Login to your account to enter"))
            }
        }
        else {
            Log.e(MyApp.ERROR_LOG_TAG, "LoginViewModel.render() -> server unavailable")
            _state.postValue(LoginState.Error("Unable to connect to remote server"))
        }
    }


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
            avatarId = user.icon
            username = user.username
            isLoginSuccessful = true

            _state.postValue(LoginState.Success(username!!, avatarId))
        } else {
            avatarId = -1
            username = null
            isLoginSuccessful = false

            _state.postValue(LoginState.UserNotFound("Invalid username or password. If you try to register, this username is unavailable"))
        }
    }

    fun saveUserLoginStatus() {
        loginStatus.saveLoginStatus(isLoginSuccessful)
    }

    fun setOutsideError(errorMessage: String) {
        _state.postValue(LoginState.Error(errorMessage))
    }

    private fun getUserLoginStatus(): Boolean {
        return if (loginStatus.getLoginStatus()) {
            getLocalUserData()
            true
        } else
            false
    }

    private fun getLocalUserData() {
        val userLocal = authUseCases.localDataUseCases.getLocalData()
        if (userLocal != null) {
            username = userLocal.username
            avatarId = userLocal.icon
        }
    }
}