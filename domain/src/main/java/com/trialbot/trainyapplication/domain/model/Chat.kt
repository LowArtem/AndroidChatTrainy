package com.trialbot.trainyapplication.domain.model

data class ChatDetails(
    val name: String,
    val about: String,
    val icon: Int,
    val secondIcon: Int,
    val creatorId: Long,
    val secondDialogMemberId: Long,
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
    val secondIcon: Int,
    val isDialog: Boolean
)

data class ChatUpdating(
    val id: Long,
    val name: String,
    val about: String,
    val icon: Int,
    val creatorId: Long
)
