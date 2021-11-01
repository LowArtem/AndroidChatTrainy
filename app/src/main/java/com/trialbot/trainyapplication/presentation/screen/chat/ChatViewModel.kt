package com.trialbot.trainyapplication.presentation.screen.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.domain.LocalDataUseCases
import com.trialbot.trainyapplication.domain.LoginStatusUseCases
import com.trialbot.trainyapplication.domain.MessageUseCases
import com.trialbot.trainyapplication.domain.StartStopRemoteActions
import com.trialbot.trainyapplication.domain.model.MessageDTO
import com.trialbot.trainyapplication.domain.model.MessageWithAuthUser
import com.trialbot.trainyapplication.domain.model.UserAuthId
import com.trialbot.trainyapplication.presentation.state.MessageState
import com.trialbot.trainyapplication.utils.default
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.cancellation.CancellationException


class ChatViewModel(
    private val loginStatus: LoginStatusUseCases,
    private val messageUseCases: MessageUseCases,
    private val localDataUseCases: LocalDataUseCases,
    private val startStopRemoteActions: StartStopRemoteActions
) : ViewModel() {
    private val _state = MutableLiveData<MessageState>().default(MessageState.Loading)
    val state: LiveData<MessageState> = _state

    // TODO: переделать через новую реализацию
    val messages: LiveData<List<MessageDTO>?> = MutableLiveData<List<MessageDTO>?>()

    private val messageObservingJob = Job()
    private val messageObservingScope = CoroutineScope(messageObservingJob + Dispatchers.IO)


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
    }


    suspend fun startMessageObserving() {
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

    fun send(input: String)
    {
        try {
            if (input.isNotBlank()) {
                viewModelScope.launch {
                    val user = localDataUseCases.getLocalData()
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

    fun applicationStarting() {
        viewModelScope.launch {
            startStopRemoteActions.appStarted()
        }
    }

    fun applicationClosing() {
        viewModelScope.launch {
            startStopRemoteActions.appClosed()
        }
        messageObservingScope.cancel()
        Thread.sleep(1000)
    }

    fun getCurrentUserId(): Long {
        return localDataUseCases.getLocalData()?.id ?: -1
    }
}