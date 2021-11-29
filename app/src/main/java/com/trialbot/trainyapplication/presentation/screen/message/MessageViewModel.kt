package com.trialbot.trainyapplication.presentation.screen.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trialbot.trainyapplication.domain.ChatGettingUseCases
import com.trialbot.trainyapplication.domain.LocalDataUseCases
import com.trialbot.trainyapplication.domain.MessageEditUseCases
import com.trialbot.trainyapplication.domain.MessageSendingUseCases
import com.trialbot.trainyapplication.domain.model.MessageDTO
import com.trialbot.trainyapplication.domain.model.MessageWithAuthUser
import com.trialbot.trainyapplication.domain.model.UserAuthId
import com.trialbot.trainyapplication.domain.model.UserLocal
import com.trialbot.trainyapplication.domain.utils.logE
import com.trialbot.trainyapplication.presentation.screen.chatProfile.UserType
import com.trialbot.trainyapplication.presentation.screen.message.recycler.MessageItemMenuClick
import com.trialbot.trainyapplication.presentation.screen.message.recycler.MessageItemMenuOptions
import com.trialbot.trainyapplication.utils.BooleanState
import com.trialbot.trainyapplication.utils.MutableBooleanState
import com.trialbot.trainyapplication.utils.default
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.cancellation.CancellationException


class MessageViewModel(
    private val messageSendingUseCases: MessageSendingUseCases,
    private val localDataUseCases: LocalDataUseCases,
    private val chatGettingUseCases: ChatGettingUseCases,
    private val messageEditUseCases: MessageEditUseCases
) : ViewModel(), MessageItemMenuClick {

    private val _state = MutableLiveData<MessageState>().default(MessageState.Loading)
    val state: LiveData<MessageState> = _state

    private var messagesCash: List<MessageDTO> = emptyList()

    private val _messages = MutableLiveData<List<MessageDTO>>()
    val messages: LiveData<List<MessageDTO>> = _messages

    private val _isMessageDeleted = MutableBooleanState().default(null)
    val isMessageDeleted: BooleanState = _isMessageDeleted

    private val messageObservingScope = CoroutineScope(Job() + Dispatchers.IO)

    var chatId: Long? = null
        private set

    var currentUser: UserLocal? = null
        private set

    var needAutoScroll = true

    private val _userType = MutableLiveData<String?>().default(null)
    val userType: LiveData<String?> = _userType


    // Main activity control function
    fun render(chatId: Long) {
        this.chatId = chatId

        try {
            currentUser = localDataUseCases.getLocalData()
                ?: throw Exception("User local auth not found")

            messageObservingScope.launch {
                val gotMessages = messageSendingUseCases.getNewMessages(chatId)
                _messages.postValue(gotMessages)

                if (gotMessages.isEmpty()) {
                    _state.postValue(MessageState.Empty)
                }
                else {
                    _state.postValue(MessageState.Success(gotMessages))
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
                if (chatId == null) delay(3000)

                val gotMessages = messageSendingUseCases.getNewMessages(chatId!!)
                if (isContentChanged(gotMessages)) {
                    _messages.postValue(gotMessages)
                }
                delay(3000)
            }
        } catch (e1: CancellationException) {}
        catch (e2: Exception) {
            logE(e2.localizedMessage ?: "Some error")

            _state.postValue(
                MessageState.Error("Message getting error")
            )
        }
    }

    fun send(input: String)
    {
        try {
            if (input.isNotBlank()) {
                viewModelScope.launch {
                    if (chatId == null) throw NullPointerException("ChatId was null when sending")

                    messageSendingUseCases.sendMessage(
                        chatId!!,
                        MessageWithAuthUser(
                            input,
                            UserAuthId(currentUser!!.id, currentUser!!.username, currentUser!!.password),
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


    override fun executeMessageMenuItemAction(messageId: Long, menuOption: MessageItemMenuOptions) {
        viewModelScope.launch {
            when (menuOption) {
                MessageItemMenuOptions.DELETE -> {
                    _isMessageDeleted.postValue(messageEditUseCases.deleteMessage(
                        chatId = chatId ?: -1,
                        messageId = messageId,
                        currentUserId = getCurrentUserId()
                    ))
                    val gotMessages = messageSendingUseCases.getNewMessages(chatId ?: -1)
                    _messages.postValue(gotMessages)
                    needAutoScroll = false
                }
            }
        }
    }

    fun applicationClosing() {
        messageObservingScope.cancel()
    }

    fun getCurrentUserId(): Long {
        return localDataUseCases.getLocalData()?.id ?: -1
    }

    fun messagesAreNoLongerEmpty(messages: List<MessageDTO>) {
        _state.postValue(MessageState.Success(messages))
    }



    private fun isContentChanged(newMessages: List<MessageDTO>): Boolean {
        if (messagesCash.isNullOrEmpty() && !newMessages.isNullOrEmpty()) {
            messagesCash = newMessages
            return true
        }

        if (newMessages.isNullOrEmpty() && !messagesCash.isNullOrEmpty()) {
            messagesCash = newMessages
            return true
        }
        if (newMessages.isNullOrEmpty() && messagesCash.isNullOrEmpty()) return false

        val result = !areMessagesEqual(messagesCash.last(), newMessages.last())
        return if (result) {
            messagesCash = newMessages
            true
        } else false
    }

    private fun areMessagesEqual(message1: MessageDTO, message2: MessageDTO): Boolean {
        return message1.text == message2.text &&
                message1.pubDate == message2.pubDate &&
                message1.author.id == message2.author.id &&
                message1.author.username == message2.author.username &&
                message1.author.icon == message2.author.icon
    }

    fun getUserType(chatId: Long): String = runBlocking(Dispatchers.IO) {
        return@runBlocking when {
            chatGettingUseCases.checkIsCreator(chatId, getCurrentUserId()) == true -> {
                _userType.postValue(UserType.Creator.toString())
                UserType.Creator.toString()
            }
            chatGettingUseCases.checkIsAdmin(chatId, getCurrentUserId()) == true -> {
                _userType.postValue(UserType.Admin.toString())
                UserType.Admin.toString()
            }
            else -> {
                _userType.postValue(UserType.Member.toString())
                UserType.Member.toString()
            }
        }
    }

    fun isUserAdminOrCreator(type: String? = null): Boolean {
        return if (type == null) {
            userType.value == UserType.Admin.toString() || userType.value == UserType.Creator.toString()
        } else {
            type == UserType.Admin.toString() || type == UserType.Creator.toString()
        }
    }
}