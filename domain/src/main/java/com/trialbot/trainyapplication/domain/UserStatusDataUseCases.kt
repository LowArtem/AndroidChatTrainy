package com.trialbot.trainyapplication.domain

import com.trialbot.trainyapplication.domain.interfaces.UserControllerRemote
import java.util.*

class UserStatusDataUseCases(private val userControllerRemote: UserControllerRemote) {

    suspend fun getUserIsOnline(id: Long): Boolean {
        return userControllerRemote.getUserIsOnline(id)
    }

    suspend fun getUserLastDate(id: Long): Date? {
        return userControllerRemote.getUserLastDate(id)
    }
}