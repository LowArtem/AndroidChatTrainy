package com.trialbot.trainyapplication.presentation.screen.login

import android.net.ConnectivityManager
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.trialbot.trainyapplication.domain.AuthUseCases
import com.trialbot.trainyapplication.domain.LocalDataUseCases
import com.trialbot.trainyapplication.domain.LoginStatusUseCases
import com.trialbot.trainyapplication.domain.StartStopRemoteActions
import com.trialbot.trainyapplication.domain.model.UserWithoutPassword
import com.trialbot.trainyapplication.domain.utils.logE
import com.trialbot.trainyapplication.utils.default
import com.trialbot.trainyapplication.utils.isInternetAvailable
import com.trialbot.trainyapplication.utils.isServerAvailable
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginStatus: LoginStatusUseCases,
    private val authUseCases: AuthUseCases,
    private val localDataUseCases: LocalDataUseCases,
    private val startStopRemoteActions: StartStopRemoteActions
) : ViewModel() {

    private val _state = MutableLiveData<LoginState>().default(LoginState.Loading)
    val state: LiveData<LoginState> = _state

    var username: String? = null
        private set

    var avatarId: Int = -1
        private set

    var userId: Long = -1
        private set

    private var isLoginSuccessful: Boolean = false


    fun render(connectivityManager: ConnectivityManager, firebaseAnalytics: FirebaseAnalytics) = viewModelScope.launch {
        var isServerAvailable = false

        if (isInternetAvailable(connectivityManager)) {
            isServerAvailable = isServerAvailable(connectivityManager)
        }
        else {
            logE("LoginViewModel.render() -> internet unavailable")
            _state.postValue(LoginState.Error("Internet connection is unavailable"))
        }

        if (isServerAvailable) {
            if (getUserLoginStatus()) {

                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.VALUE, "auto")
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)

                isLoginSuccessful = true
                _state.postValue(LoginState.Success(username!!, avatarId))
            } else {
                isLoginSuccessful = false
                _state.postValue(LoginState.UserNotFound("Login to your account to enter"))
            }
        }
        else {
            logE("LoginViewModel.render() -> server unavailable")
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
            userId = user.id
            isLoginSuccessful = true

            _state.postValue(LoginState.Success(username!!, avatarId))
        } else {
            avatarId = -1
            userId = -1
            username = null
            isLoginSuccessful = false

            _state.postValue(LoginState.UserNotFound("Invalid username or password. If you try to register, this username is unavailable"))
        }
    }

    fun applicationStarted() {
        viewModelScope.launch {
            startStopRemoteActions.appStarted()
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
        val userLocal = localDataUseCases.getLocalData()
        if (userLocal != null) {
            username = userLocal.username
            avatarId = userLocal.icon
            userId = userLocal.id
        }
    }
}