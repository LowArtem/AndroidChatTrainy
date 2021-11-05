package com.trialbot.trainyapplication.domain.model

data class ChatDTO(
    val id: Long,
    val name: String,
    val about: String,
    val icon: Int,
    val members: List<UserWithoutPassword>,
    val messages: List<MessageDTO>,
    val secondDialogMemberId: Long,
    val creatorId: Long,
    val adminIds: List<Long>
)

data class ChatDetails(
    val name: String,
    val about: String,
    val icon: Int,
    val secondDialogMemberId: Long,
    val creatorId: Long,
)

data class ChatCreating(
    val name: String,
    val about: String,
    val icon: Int,
    val creatorId: Long,
)

data class ChatInfo(
    val id: Long,
    val name: String,
    val icon: Int,
    val isDialog: Boolean
)

data class ChatUpdating(
    val id: Long,
    val name: String,
    val about: String,
    val icon: Int,
    val creatorId: Long,
    val adminIds: List<Long>
)
