package com.trialbot.trainyapplication.domain.contract

/**
 * Implement this interface in your fragment if you want to override default toolbar title
 */
interface HasCustomTitle {
    fun getTitle(): String
}