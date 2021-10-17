package com.trialbot.trainyapplication.data.model

import java.util.*

data class UserFull(
    val id: Long,
    val icon: String?,
    val username: String,
    val password: String,
    val isOnline: Boolean,
    val lastDate: Date
)

data class UserWithoutPassword(
    val id: Long,
    val icon: String,
    val username: String,
    val isOnline: Boolean,
    val lastDate: Date
)

data class UserWithoutIcon(
    val id: Long,
    val username: String,
    val password: String,
    val isOnline: Boolean,
    val lastDate: Date?
)

data class UserAuth(
    val username: String,
    val password: String
)

data class UserAuthId(
    val id: Long,
    val username: String,
    val password: String
)

data class UserMessage(
    val id: Long,
    val icon: String,
    val username: String
)

data class UserMessageWithoutIcon(
    val id: Long,
    val username: String
)