package com.trialbot.trainyapplication.data.model

import androidx.annotation.DrawableRes
import java.util.*

interface User

data class UserFull(
    val id: Long,
    @DrawableRes
    val icon: Int,
    val username: String,
    val password: String,
    val isOnline: Boolean,
    val lastDate: Date
) : User

data class UserLocal(
    val id: Long,
    val username: String,
    val password: String,
    @DrawableRes
    val icon: Int
) : User

data class UserWithoutPassword(
    val id: Long,
    @DrawableRes
    val icon: Int,
    val username: String,
    val isOnline: Boolean,
    val lastDate: Date
) : User

data class UserWithoutIcon(
    val id: Long,
    val username: String,
    val password: String,
    val isOnline: Boolean,
    val lastDate: Date?
) : User

data class UserAuth(
    val username: String,
    val password: String
) : User

data class UserAuthId(
    val id: Long,
    val username: String,
    val password: String
) : User

data class UserMessage(
    val id: Long,
    @DrawableRes
    val icon: Int,
    val username: String
) : User

data class UserMessageWithoutIcon(
    val id: Long,
    val username: String
) : User