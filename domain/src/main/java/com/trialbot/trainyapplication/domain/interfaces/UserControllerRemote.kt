package com.trialbot.trainyapplication.domain.interfaces

import com.trialbot.trainyapplication.domain.model.UserFull
import com.trialbot.trainyapplication.domain.model.UserWithoutIcon
import java.util.*

interface UserControllerRemote {

    suspend fun getUserIsOnline(id: Long): Boolean

    suspend fun getUserLastDate(id: Long): Date?

    suspend fun updateUser(id: Long, user: UserFull): Boolean

    suspend fun deleteUser(user: UserWithoutIcon): Boolean
}