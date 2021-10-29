package com.trialbot.trainyapplication.domain.contract

import androidx.annotation.DrawableRes

/**
 * Implement this interface in your fragment if you want to override default toolbar icon
 * set null, if you don't want to see an icon
 */
interface HasCustomAppbarIcon {
    @DrawableRes fun getIconRes(): Int?
}