package com.trialbot.trainyapplication.presentation.screen.chatProfile

sealed class UserType {
    object Creator : UserType() {
        override fun toString(): String {
            return "creator"
        }
    }

    object Admin : UserType() {
        override fun toString(): String {
            return "admin"
        }
    }

    object Member : UserType() {
        override fun toString(): String {
            return "member"
        }
    }

    companion object {
        fun fromString(string: String): UserType {
            return when (string) {
                Creator.toString() -> Creator
                Admin.toString() -> Admin
                Member.toString() -> Member
                else -> Member
            }
        }
    }
}
