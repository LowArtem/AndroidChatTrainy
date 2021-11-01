package com.trialbot.trainyapplication.presentation.state

import androidx.annotation.DrawableRes
import com.trialbot.trainyapplication.domain.model.AvatarItem
import com.trialbot.trainyapplication.domain.model.UserFull
import com.trialbot.trainyapplication.domain.model.UserWithoutPassword

sealed class ProfileState {
    object Loading : ProfileState()
    data class ReadOnly(val user: UserWithoutPassword) : ProfileState()
    data class ReadWrite(val user: UserFull) : ProfileState()
    //object AvatarChangingLoading : ProfileState() // если аватары будут приходить динамически
    data class AvatarChangingOpened(val avatarList: List<AvatarItem>) : ProfileState()
    data class AvatarChangingClosing(@DrawableRes val newAvatarId: Int) : ProfileState()
    data class Error(val errorText: String) : ProfileState()
}