package com.trialbot.trainyapplication.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.trialbot.trainyapplication.domain.interfaces.MessageControllerRemote
import com.trialbot.trainyapplication.domain.model.MessageDTO

class MessagesPageSource(
    private val messageControllerRemote: MessageControllerRemote,
    private val chatId: Long,
    private val onPageLoaded: ((currentStartIndex: Int) -> Unit)
) : PagingSource<Int, MessageDTO>() {

    override fun getRefreshKey(state: PagingState<Int, MessageDTO>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MessageDTO> {
        val startIndex = params.key ?: 0
        try {
            val pageSize = params.loadSize
            val messages = messageControllerRemote.getMessagesPage(chatId, pageSize, startIndex)

            if (messages != null && messages.isNotEmpty()) {
                val nextStartIndex = if (messages.size < pageSize) null else startIndex + pageSize
                val prevStartIndex = if (startIndex == 0) null else startIndex - pageSize

                onPageLoaded(startIndex)
                return LoadResult.Page(messages, prevStartIndex, nextStartIndex)
            } else {
                val nextStartIndex = null
                val prevStartIndex = if (startIndex == 0) null else startIndex - pageSize

                onPageLoaded(startIndex)
                return LoadResult.Page(emptyList(), prevStartIndex, nextStartIndex)
            }
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}