package com.trialbot.trainyapplication.presentation.screen.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trialbot.trainyapplication.domain.LocalDataUseCases
import com.trialbot.trainyapplication.domain.LoginStatusUseCases
import com.trialbot.trainyapplication.domain.MessageUseCases
import com.trialbot.trainyapplication.domain.StartStopRemoteActions
import com.trialbot.trainyapplication.domain.model.MessageDTO
import com.trialbot.trainyapplication.domain.model.MessageWithAuthUser
import com.trialbot.trainyapplication.domain.model.UserAuthId
import com.trialbot.trainyapplication.domain.utils.logE
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

    private val _messages = MutableLiveData<List<MessageDTO>?>()
    val messages: LiveData<List<MessageDTO>?> = _messages

    private val messageObservingJob = Job()
    private val messageObservingScope = CoroutineScope(messageObservingJob + Dispatchers.IO)


    // Main activity control function
    fun render() {
        try {
            messageObservingScope.launch {
                val initMessagesJob = viewModelScope.launch {
                    val gotMessages = messageUseCases.updateMessages()
                    if (gotMessages != null) {
                        _messages.postValue(gotMessages)
                    }
                }
                initMessagesJob.join()
                if (_messages.value == null || _messages.value!!.isEmpty()) {
                    _state.postValue(MessageState.Empty)
                }
                else {
                    _state.postValue(MessageState.Success(_messages.value!!))
                }
            }
        } catch (e: Exception) {
            logE(e.localizedMessage ?: "Some error")
            _state.postValue(MessageState.Error(e.localizedMessage?.toString() ?: "Message getting error"))
        }
    }


    suspend fun startMessageObserving() {
        try {
            while (true) {
                val gotMessages = messageUseCases.updateMessages()
                if (gotMessages != null) {
                    _messages.postValue(gotMessages)
                }
                delay(3000)
            }
        } catch (e1: CancellationException) {

        } catch (e2: Exception) {
            logE(e2.localizedMessage ?: "Some error")

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
            logE(e.localizedMessage ?: "Some error")
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