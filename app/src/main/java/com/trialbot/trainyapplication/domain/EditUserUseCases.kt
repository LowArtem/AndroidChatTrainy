package com.trialbot.trainyapplication.domain

import android.content.SharedPreferences
import androidx.annotation.DrawableRes
import com.trialbot.trainyapplication.data.UserControllerRemote
import com.trialbot.trainyapplication.data.model.UserFull
import com.trialbot.trainyapplication.data.model.UserLocal
import com.trialbot.trainyapplication.data.remote.chatServer.ChatApi

class EditUserUseCases(
    chatApi: ChatApi,
    sharedPreferences: SharedPreferences
) {

    private val userControllerRemote = UserControllerRemote(chatApi)
    private val localDataUseCases = LocalDataUseCases(sharedPreferences)

    suspend fun changePassword(user: UserFull, newPassword: String): Boolean {
        if (newPassword.isBlank()) return false

        val user = user.copy(password = newPassword)

        return if (userControllerRemote.updateUser(user.id, user)) {
            localDataUseCases.saveLocalData(UserLocal(user.id, user.username, user.password, user.icon))
            true
        } else false
    }

    suspend fun changeIcon(user: UserFull, @DrawableRes newIcon: Int): Boolean {
        if (newIcon == -1) return false

        val user = user.copy(icon = newIcon)

        return if (userControllerRemote.updateUser(user.id, user)) {
            localDataUseCases.saveLocalData(UserLocal(user.id, user.username, user.password, user.icon))
            true
        } else false
    }
}