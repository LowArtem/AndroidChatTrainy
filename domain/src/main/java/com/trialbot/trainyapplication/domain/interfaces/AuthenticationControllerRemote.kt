package com.trialbot.trainyapplication.domain.interfaces

import com.trialbot.trainyapplication.domain.model.UserAuth
import com.trialbot.trainyapplication.domain.model.UserFull
import com.trialbot.trainyapplication.domain.model.UserWithoutPassword

interface AuthenticationControllerRemote {
    suspend fun getUserByAuthData(userAuth: UserAuth): UserWithoutPassword?

    suspend fun createUser(userAuth: UserAuth): UserFull?

    suspend fun updateUser(id: Long, user: UserFull): Boolean

    suspend fun deleteUser(id: Long, userAuth: UserAuth): Boolean
}