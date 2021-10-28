package com.trialbot.trainyapplication.presentation.state

import com.trialbot.trainyapplication.data.model.UserFull
import com.trialbot.trainyapplication.data.model.UserWithoutPassword

sealed class ProfileState {
    object Loading : ProfileState()
    data class ReadOnly(val user: UserWithoutPassword): ProfileState()
    data class ReadWrite(val user: UserFull): ProfileState()
    data class Error(val errorText: String) : ProfileState()
}