package com.trialbot.trainyapplication.presentation.screen.chatProfile

sealed class ChatProfileState {

    object Loading : ChatProfileState()

    data class Creator(
        val chatName: String,
        val chatIcon: Int,
        val isDialog: Boolean
    ) : ChatProfileState()

    data class Admin(
        val chatName: String,
        val chatIcon: Int
    ) : ChatProfileState()

    data class Member(
        val chatName: String,
        val chatIcon: Int
    ) : ChatProfileState()

    data class Error(val errorText: String) : ChatProfileState()
}