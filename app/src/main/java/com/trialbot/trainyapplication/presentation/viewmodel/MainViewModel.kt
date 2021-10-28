package com.trialbot.trainyapplication.presentation.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.lifecycle.*
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.data.AuthenticationControllerLocal
import com.trialbot.trainyapplication.data.AuthenticationControllerRemote
import com.trialbot.trainyapplication.data.model.MessageDTO
import com.trialbot.trainyapplication.data.model.MessageWithAuthUser
import com.trialbot.trainyapplication.data.model.UserAuthId
import com.trialbot.trainyapplication.data.remote.chatServer.ChatApi
import com.trialbot.trainyapplication.domain.LocalDataUseCases
import com.trialbot.trainyapplication.domain.LoginStatusUseCases
import com.trialbot.trainyapplication.domain.MessageUseCases
import com.trialbot.trainyapplication.domain.StartStopRemoteActions
import com.trialbot.trainyapplication.presentation.state.MessageState
import com.trialbot.trainyapplication.utils.default
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.cancellation.CancellationException


class MainViewModel(
    chatApi: ChatApi,
    sharedPrefs: SharedPreferences
) : ViewModel() {

    class MainViewModelFactory(
        private val chatApi: ChatApi,
        private val sharedPrefs: SharedPreferences
    ) : ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(chatApi, sharedPrefs) as T
        }
    }

    private val _state = MutableLiveData<MessageState>().default(MessageState.Loading)
    val state: LiveData<MessageState> = _state


    private val loginStatus = LoginStatusUseCases(sharedPrefs)

    private val authControllerLocal = AuthenticationControllerLocal(sharedPrefs)

    private val startStopRemoteActions = StartStopRemoteActions(
        AuthenticationControllerRemote(chatApi),
        authControllerLocal
    )

    private val messageUseCases = MessageUseCases(chatApi)
    private val localDataUseCases = LocalDataUseCases(sharedPrefs)

    val messages: LiveData<List<MessageDTO>> = messageUseCases.messages


    private val messageObservingScope = CoroutineScope(Job() + Dispatchers.IO)

    private var userId: Int = -1


    // Main activity control function
    fun render() {
        try {
            messageObservingScope.launch {
                val initMessagesJob = viewModelScope.launch {
                    messageUseCases.updateMessages()
                }
                initMessagesJob.join()
                if (messages.value == null || messages.value!!.isEmpty()) {
                    _state.postValue(MessageState.Empty)
                }
                else {
                    _state.postValue(MessageState.Success(messages.value!!))
                }
            }
        } catch (e: Exception) {
            Log.e(MyApp.ERROR_LOG_TAG, "MainViewModel.render() -> ${e.localizedMessage}")
            _state.postValue(MessageState.Error(e.localizedMessage?.toString() ?: "Message getting error"))
        }

        startMessageObserving()
    }


    private fun startMessageObserving() {
        messageObservingScope.launch {
            try {
                while (true) {
                    messageUseCases.updateMessages()
                    delay(3000)
                }
            } catch (e1: CancellationException) {

            } catch (e2: Exception) {
                Log.e(MyApp.ERROR_LOG_TAG, "MainViewModel.startMessageObserving() -> ${e2.localizedMessage}")

                _state.postValue(
                    MessageState.Error(
                        e2.localizedMessage?.toString() ?: "Message getting error"
                    )
                )
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
                viewModelScope.launch {
                    val user = authControllerLocal.getCredentials()
                        ?: throw Exception("User local auth not found")
                    messageUseCases.sendMessage(
                        MessageWithAuthUser(
                            input,
                            UserAuthId(user.id, user.username, user.password),
                            Calendar.getInstance().time
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(MyApp.ERROR_LOG_TAG, "MainViewModel.send() -> ${e.localizedMessage}")
            _state.postValue(MessageState.Error(e.localizedMessage?.toString() ?: "Message sending error"))
        }
    }

    fun logOut() {
        loginStatus.saveLoginStatus(false)
    }

    fun applicationClosing() {
        viewModelScope.launch {
            startStopRemoteActions.appClosed(userId)
        }
        Thread.sleep(1000)
    }

    fun getCurrentUserId(): Long {
        return localDataUseCases.getLocalData()?.id ?: -1
    }

    fun setUserIconId(@DrawableRes id: Int) {
        userId = id
    }
}