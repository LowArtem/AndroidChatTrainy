package com.trialbot.trainyapplication.presentation.state


sealed class LoginState {
    object Loading : LoginState()
    data class UserNotFound(val message: String): LoginState()
    data class Success(val username: String, val avatarId: Int) : LoginState()
    data class Error(val errorText: String) : LoginState()
}