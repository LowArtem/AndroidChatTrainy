package com.trialbot.trainyapplication.presentation.screen.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trialbot.trainyapplication.domain.ChatGettingUseCases
import com.trialbot.trainyapplication.domain.LocalDataUseCases
import com.trialbot.trainyapplication.domain.MessageEditUseCases
import com.trialbot.trainyapplication.domain.MessageSendingUseCases
import com.trialbot.trainyapplication.domain.model.*
import com.trialbot.trainyapplication.domain.utils.logE
import com.trialbot.trainyapplication.presentation.screen.chatProfile.UserType
import com.trialbot.trainyapplication.presentation.screen.message.recycler.AdminChecking
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
) : ViewModel(), MessageItemMenuClick, AdminChecking {

    private val _state = MutableLiveData<MessageState>().default(MessageState.Loading)
    val state: LiveData<MessageState> = _state

//    private var messagesCash: List<MessageDTO> = emptyList()

    private val _messages = MutableLiveData<List<MessageDTO>?>().default(null)
    val messages: LiveData<List<MessageDTO>?> = _messages

    private val _isNeedToRedrawRecycler = MutableBooleanState().default(null)
    val isNeedToRedrawRecycler: BooleanState = _isNeedToRedrawRecycler

    private val _isMessageDeleted = MutableBooleanState().default(null)
    val isMessageDeleted: BooleanState = _isMessageDeleted

    private val messageObservingScope = CoroutineScope(Job() + Dispatchers.IO)

    var chatId: Long? = null
        private set

    var currentUser: UserLocal? = null
        private set

    var needAutoScroll = true


    private var adminIds: List<Long>? = null
    private var creatorIds: MutableList<Long>? = null
    private var currentChat: ChatDetails? = null


    // Main activity control function
    fun render(chatId: Long) {
        if (_messages.value == null) {
            this.chatId = chatId

            try {
                currentUser = localDataUseCases.getLocalData()
                    ?: throw Exception("User local auth not found")

                viewModelScope.launch {
                    if (initChat(chatId) == null) throw Exception("Cannot get the current chat")
                }

                messageObservingScope.launch {
                    val gotMessages = messageSendingUseCases.getNewMessages(chatId)
                    _messages.postValue(gotMessages)

                    if (gotMessages.isEmpty()) {
                        _state.postValue(MessageState.Empty)
                    } else {
                        _state.postValue(MessageState.Success(gotMessages))
                    }
                }
            } catch (e: Exception) {
                logE(e.localizedMessage ?: "Some error")
                _state.postValue(
                    MessageState.Error(
                        e.localizedMessage?.toString() ?: "Message getting error"
                    )
                )
            }
        }
    }

    private suspend fun initChat(chatId: Long): ChatDetails? {
        if (currentChat == null) {
            currentChat = chatGettingUseCases.openChat(chatId)
        }
        return currentChat
    }


    suspend fun startMessageObserving() {
        try {
            while (true) {
                if (chatId == null) delay(3000)

                adminIds = chatGettingUseCases.getAdminIds(chatId!!)
                val gotMessages = messageSendingUseCases.getNewMessages(chatId!!)
                _messages.postValue(gotMessages)
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

//    fun updateAdmins() {
//        viewModelScope.launch {
//            try {
//                adminIds = chatGettingUseCases.getAdminIds(chatId!!)
//                _isNeedToRedrawRecycler.postValue(true)
//            } catch (e1: CancellationException) {
//            } catch (e2: Exception) {
//                logE(e2.localizedMessage ?: "Some error")
//
//                _state.postValue(
//                    MessageState.Error("Admins getting error")
//                )
//            }
//        }
//    }

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

    fun getUserType(chatId: Long, userId: Long = getCurrentUserId()): String = runBlocking(Dispatchers.IO) {
        val chat = initChat(chatId) ?: return@runBlocking UserType.Member.toString()

        if (adminIds == null) {
            adminIds = chatGettingUseCases.getAdminIds(chatId).toMutableList()
        }

        if (creatorIds.isNullOrEmpty()) creatorIds = mutableListOf(chatGettingUseCases.openChat(chatId)?.creatorId ?:
        return@runBlocking UserType.Member.toString())

        if (chat.secondDialogMemberId != -1L) creatorIds?.add(chat.secondDialogMemberId)

        return@runBlocking when {
            creatorIds?.contains(userId) ?: false -> {
                UserType.Creator.toString()
            }
            adminIds?.contains(userId) ?: false -> {
                UserType.Admin.toString()
            }
            else -> {
                UserType.Member.toString()
            }
        }
    }

    fun isUserAdminOrCreator(chatId: Long, type: String? = null): Boolean {
        if (currentChat?.secondDialogMemberId != -1L) return false

        return if (type == null) {
            getUserType(chatId) == UserType.Admin.toString() || getUserType(chatId) == UserType.Creator.toString()
        } else {
            type == UserType.Admin.toString() || type == UserType.Creator.toString()
        }
    }

    fun clearResult() {
        _isMessageDeleted.postValue(null)
        _isNeedToRedrawRecycler.postValue(null)
    }

    override fun isUserAdmin(userId: Long): Boolean {
        return getUserType(chatId ?: -1, userId) == UserType.Admin.toString()
    }
}