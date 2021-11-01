package com.trialbot.trainyapplication.domain.contract

/**
 * Implement this interface in your fragment if you want to override default toolbar icon
 * set null, if you don't want to see an icon
 */
interface HasCustomAppbarIcon {
    fun getIconRes(): Int?
}