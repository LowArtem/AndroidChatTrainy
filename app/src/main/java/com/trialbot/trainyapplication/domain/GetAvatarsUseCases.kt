package com.trialbot.trainyapplication.domain

import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.presentation.recycler.avatar.model.AvatarItem
import java.util.*

class GetAvatarsUseCases {

    fun getAvatars() : List<AvatarItem> {
        val avatarItems = ArrayList<AvatarItem>()
        avatarItems.add(AvatarItem(R.drawable.ic_avatar_female1, "Female 1"))
        avatarItems.add(AvatarItem(R.drawable.ic_avatar_female2, "Female 2"))
        avatarItems.add(AvatarItem(R.drawable.ic_avatar_female3, "Female 3"))
        avatarItems.add(AvatarItem(R.drawable.ic_avatar_female4, "Female 4"))
        avatarItems.add(AvatarItem(R.drawable.ic_avatar_female5, "Female 5"))
        avatarItems.add(AvatarItem(R.drawable.ic_avatar_female6, "Female 6"))
        avatarItems.add(AvatarItem(R.drawable.ic_avatar_female7, "Female 7"))

        avatarItems.add(AvatarItem(R.drawable.ic_avatar_male1, "Male 1"))
        avatarItems.add(AvatarItem(R.drawable.ic_avatar_male2, "Male 2"))
        avatarItems.add(AvatarItem(R.drawable.ic_avatar_male3, "Male 3"))
        avatarItems.add(AvatarItem(R.drawable.ic_avatar_male4, "Male 4"))
        avatarItems.add(AvatarItem(R.drawable.ic_avatar_male5, "Male 5"))
        avatarItems.add(AvatarItem(R.drawable.ic_avatar_male6, "Male 6"))
        avatarItems.add(AvatarItem(R.drawable.ic_avatar_male7, "Male 7"))
        avatarItems.add(AvatarItem(R.drawable.ic_avatar_male8, "Male 8"))
        avatarItems.add(AvatarItem(R.drawable.ic_avatar_male9, "Male 9"))

        return avatarItems
    }
}