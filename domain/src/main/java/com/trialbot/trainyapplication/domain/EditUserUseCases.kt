package com.trialbot.trainyapplication.domain

import com.trialbot.trainyapplication.domain.interfaces.UserControllerRemote
import com.trialbot.trainyapplication.domain.model.UserFull
import com.trialbot.trainyapplication.domain.model.UserLocal


class EditUserUseCases(
    private val userControllerRemote: UserControllerRemote,
    private val localDataUseCases: LocalDataUseCases
) {
    suspend fun changePassword(user: UserFull, newPassword: String): Boolean {
        if (newPassword.isBlank()) return false

        val user = user.copy(password = newPassword)

        return if (userControllerRemote.updateUser(user.id, user)) {
            localDataUseCases.saveLocalData(UserLocal(user.id, user.username, user.password, user.icon))
            true
        } else false
    }

    suspend fun changeIcon(user: UserFull, newIcon: Int): Boolean {
        if (newIcon == -1) return false

        val user = user.copy(icon = newIcon)

        return if (userControllerRemote.updateUser(user.id, user)) {
            localDataUseCases.saveLocalData(UserLocal(user.id, user.username, user.password, user.icon))
            true
        } else false
    }
}