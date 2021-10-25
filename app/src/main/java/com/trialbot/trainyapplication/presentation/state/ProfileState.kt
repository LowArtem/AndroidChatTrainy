package com.trialbot.trainyapplication.presentation.state

import com.trialbot.trainyapplication.data.model.UserFull

sealed class ProfileState {
    object Loading : ProfileState()
    data class ReadOnly(val user: UserFull): ProfileState()
    data class ReadWrite(val user: UserFull): ProfileState()
    data class Error(val errorText: String) : ProfileState()
}