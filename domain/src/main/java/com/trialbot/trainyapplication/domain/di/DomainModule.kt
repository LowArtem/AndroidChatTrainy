package com.trialbot.trainyapplication.domain.di

import com.trialbot.trainyapplication.domain.*
import org.koin.dsl.module

val domainModule = module {
    factory {
        AuthUseCases(
            authControllerRemote = get(),
            localDataUseCases = get()
        )
    }

    factory {
        EditUserUseCases(
            userControllerRemote = get(),
            localDataUseCases = get()
        )
    }

    factory {
        LocalDataUseCases(
            authControllerLocal = get()
        )
    }

    factory {
        LoginStatusUseCases(
            loginStatusControllerLocal = get()
        )
    }

    factory {
        StartStopRemoteActions(
            authControllerRemote = get(),
            authControllerLocal = get()
        )
    }

    factory {
        UserStatusDataUseCases(
            userControllerRemote = get()
        )
    }

    factory {
        MessageSendingUseCases(
            messageControllerRemote = get()
        )
    }

    factory {
        ChatEditingUseCases(
            chatControllerRemote = get()
        )
    }

    factory {
        ChatGettingUseCases(
            chatControllerRemote = get(),
            localDataUseCases = get()
        )
    }

    factory {
        FindUsersUseCases(
            userControllerRemote = get()
        )
    }
}