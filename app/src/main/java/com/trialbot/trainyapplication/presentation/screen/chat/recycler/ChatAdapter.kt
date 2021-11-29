package com.trialbot.trainyapplication.presentation.screen.chat.recycler

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.databinding.ItemChatBinding
import com.trialbot.trainyapplication.domain.model.ChatInfo
import com.trialbot.trainyapplication.domain.utils.DIALOG_DIVIDER
import com.trialbot.trainyapplication.presentation.drawable.DrawableController
import com.trialbot.trainyapplication.utils.BaseViewHolder


class ChatDiffCallback(
    private val oldChats: List<ChatInfo>,
    private val newChats: List<ChatInfo>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldChats.size

    override fun getNewListSize(): Int = newChats.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldChats[oldItemPosition].id == newChats[newItemPosition].id &&
                oldChats[oldItemPosition].name == newChats[newItemPosition].name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldChats[oldItemPosition] == newChats[newItemPosition]
    }
}

interface ChatAdapterClickAction {
    fun clickChat(chatId: Long, chatName: String, chatIconId: Int)
}

class ChatAdapter(
    private val resources: Resources,
    private val clickAction: ChatAdapterClickAction,
    private val username: String
) : RecyclerView.Adapter<BaseViewHolder<ChatInfo>>() {

    private var chats: MutableList<ChatInfo> = mutableListOf()
        set(newValue) {
            val diffCallback = ChatDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field.clear()
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    class ChatsViewHolder(
        private val binding: ItemChatBinding,
        private val username: String,
    ) : BaseViewHolder<ChatInfo>(binding.root) {
        override fun bind(item: ChatInfo, resources: Resources) {
            with(this.binding) {
                chatNameTV.text = getChatName(username, item)
                chatIconIV.setImageDrawable(DrawableController.getDrawableFromId(
                    getChatIcon(username, item),
                    resources
                ))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ChatInfo> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChatBinding.inflate(inflater, parent, false)
        return ChatsViewHolder(binding, this.username)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ChatInfo>, position: Int) {
        holder.bind(chats[position], resources)
        holder.itemView.setOnClickListener {
            clickAction.clickChat(
                chats[position].id,
                getChatName(username, chats[position]),
                getChatIcon(username, chats[position])
            )
        }
    }

    override fun getItemCount(): Int = chats.size

    fun updateChats(newChats: List<ChatInfo>) {
        chats = newChats.toMutableList()
    }
}

private fun getChatIcon(username: String, chat: ChatInfo): Int {
    return if (chat.isDialog) {
        val names = chat.name.split(DIALOG_DIVIDER)
        if (username == names[0]) chat.secondIcon else chat.icon
    } else {
        if (chat.icon == -1) R.drawable.ic_default_chat else chat.icon
    }
}

private fun getChatName(username: String, chat: ChatInfo): String {
    return if (chat.isDialog) {
        val names = chat.name.split(DIALOG_DIVIDER)
        if (username == names[0]) {
            names[1]

        } else {
            names[0]
        }
    } else {
        chat.name
    }
}