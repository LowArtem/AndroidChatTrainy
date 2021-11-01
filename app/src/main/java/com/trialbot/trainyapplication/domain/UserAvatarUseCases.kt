package com.trialbot.trainyapplication.domain

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.R

object UserAvatarUseCases {

    fun getDrawableFromId(@DrawableRes id: Int, res: Resources): Drawable {

        return try {
            ResourcesCompat.getDrawable(res, id, null) ?: ResourcesCompat.getDrawable(res, R.drawable.ic_avatar_default, null)!!
        } catch (e: Resources.NotFoundException) {
            ResourcesCompat.getDrawable(res, R.drawable.ic_avatar_default, null)!!
        } catch (e: Exception) {
            Log.e(MyApp.ERROR_LOG_TAG, "UserAvatarUseCases.getDrawableFromId() -> strange error")
            ResourcesCompat.getDrawable(res, R.drawable.ic_avatar_default, null)!!
        }
    }
}