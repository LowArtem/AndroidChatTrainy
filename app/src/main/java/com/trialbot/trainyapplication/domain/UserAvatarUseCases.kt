package com.trialbot.trainyapplication.domain

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.trialbot.trainyapplication.R

class UserAvatarUseCases {

    fun getDrawableFromId(@DrawableRes id: Int, res: Resources): Drawable {
        return ResourcesCompat.getDrawable(res, id, null) ?: ResourcesCompat.getDrawable(res, R.drawable.ic_avatar_default, null)!!
    }
}