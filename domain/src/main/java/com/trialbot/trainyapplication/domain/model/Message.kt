package com.trialbot.trainyapplication.domain.model

import java.util.*

data class MessageDTO(
    val text: String,
    val author: UserMessage,
    val pubDate: Date
)

data class MessageWithoutUserIcon(
    val text: String,
    val author: UserMessageWithoutIcon,
    val pubDate: Date
)

data class MessageWithAuthUser(
    val text: String,
    val author: UserAuthId,
    val pubDate: Date
)
