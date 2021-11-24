package com.trialbot.trainyapplication.domain

import com.trialbot.trainyapplication.domain.interfaces.UserControllerRemote
import com.trialbot.trainyapplication.domain.model.UserWithoutPassword

class FindUsersUseCases(private val userControllerRemote: UserControllerRemote) {

    suspend fun findUsersByUsername(query: String): List<UserWithoutPassword> {
        return userControllerRemote.findUsersByUsername(query) ?: emptyList()
    }
}