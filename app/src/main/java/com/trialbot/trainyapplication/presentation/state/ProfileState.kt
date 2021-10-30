package com.trialbot.trainyapplication.presentation.state

import androidx.annotation.DrawableRes
import com.trialbot.trainyapplication.data.model.UserFull
import com.trialbot.trainyapplication.data.model.UserWithoutPassword
import com.trialbot.trainyapplication.presentation.recycler.avatar.model.AvatarItem

sealed class ProfileState {
    object Loading : ProfileState()
    data class ReadOnly(val user: UserWithoutPassword) : ProfileState()
    data class ReadWrite(val user: UserFull) : ProfileState()
    //object AvatarChangingLoading : ProfileState() // если аватары будут приходить динамически
    data class AvatarChangingOpened(val avatarList: List<AvatarItem>) : ProfileState()
    data class AvatarChangingClosing(@DrawableRes val newAvatarId: Int) : ProfileState()
    data class Error(val errorText: String) : ProfileState()
}