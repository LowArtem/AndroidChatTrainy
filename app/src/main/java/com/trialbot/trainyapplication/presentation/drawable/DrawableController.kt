package com.trialbot.trainyapplication.presentation.drawable

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.domain.utils.logE

object DrawableController {

    fun getDrawableFromId(id: Int, res: Resources): Drawable {
        return try {
            ResourcesCompat.getDrawable(res, id, null) ?: ResourcesCompat.getDrawable(res, R.drawable.ic_avatar_default, null)!!
        } catch (e: Resources.NotFoundException) {
            ResourcesCompat.getDrawable(res, R.drawable.ic_avatar_default, null)!!
        } catch (e: Exception) {
//            Log.e(MyApp.ERROR_LOG_TAG, "UserAvatarUseCases.getDrawableFromId() -> strange error")
            logE("Strange Error")
            ResourcesCompat.getDrawable(res, R.drawable.ic_avatar_default, null)!!
        }
    }
}