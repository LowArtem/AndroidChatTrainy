package com.trialbot.trainyapplication.presentation.viewmodel

import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.data.AuthenticationControllerLocal
import com.trialbot.trainyapplication.data.AuthenticationControllerRemote
import com.trialbot.trainyapplication.data.model.MessageWithAuthUser
import com.trialbot.trainyapplication.data.remote.chatServer.ChatApi
import com.trialbot.trainyapplication.domain.LoginStatusUseCases
import com.trialbot.trainyapplication.domain.MessageUseCases
import com.trialbot.trainyapplication.domain.StartStopRemoteActions
import com.trialbot.trainyapplication.presentation.recycler.message.MessageAdapter
import kotlinx.coroutines.*
import java.util.*

class MainViewModel(
    chatApi: ChatApi,
    sharedPrefs: SharedPreferences,
    private val adapter: MessageAdapter
) : ViewModel() {

    class MainViewModelFactory(
        private val chatApi: ChatApi,
        private val sharedPrefs: SharedPreferences,
        private val adapter: MessageAdapter
    ) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(chatApi, sharedPrefs, adapter) as T
        }
    }

    private val loginStatus = LoginStatusUseCases(sharedPrefs)

    private val authControllerLocal = AuthenticationControllerLocal(sharedPrefs)

    private val startStopRemoteActions = StartStopRemoteActions(
        AuthenticationControllerRemote(chatApi),
        authControllerLocal
    )

    private val messageUseCases = MessageUseCases(chatApi, viewModelScope)

    private val messageObservingScope = CoroutineScope(Job() + Dispatchers.IO)
    private val mainLooper = Looper.getMainLooper()

    fun startMessageObserving() {
        messageObservingScope.launch {
            while (true) {
                updateRecycler()
                delay(3000)
            }
        }
    }

    fun stopMessageObserving() {
        messageObservingScope.cancel()
    }

    fun send(input: String)
    {
        try {
            if (input.isNotBlank()) {
                val user = authControllerLocal.getCredentials() ?: throw Exception("User local auth not found")
                messageUseCases.sendMessage(MessageWithAuthUser(input, user, Calendar.getInstance().time))
            }
        } catch (e: Exception) {
            Log.e(MyApp.ERROR_LOG_TAG, "MainViewModel.send() -> ${e.localizedMessage}")
        }
    }

    fun logOut() {
        loginStatus.saveLoginStatus(false)
    }

    fun applicationClosing() {
        viewModelScope.launch {
            startStopRemoteActions.appClosed()
        }
        Thread.sleep(1000)
    }

    private fun updateRecycler() {
        messageUseCases.updateMessages()

        Handler(mainLooper).post {
            adapter.updateMessages(messageUseCases.messages.value ?: emptyList())
        }
    }
}