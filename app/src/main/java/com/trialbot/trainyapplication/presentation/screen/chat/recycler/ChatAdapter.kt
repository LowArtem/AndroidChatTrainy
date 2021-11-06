package com.trialbot.trainyapplication.presentation.screen.chat.recycler

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.databinding.ItemChatBinding
import com.trialbot.trainyapplication.domain.model.ChatInfo
import com.trialbot.trainyapplication.presentation.drawable.DrawableController


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

abstract class BaseViewHolder<T>(viewItem: View) : RecyclerView.ViewHolder(viewItem) {
    abstract fun bind(item: T, resources: Resources)
}

interface ChatAdapterClickAction {
    fun openChat(chatId: Long)
}

class ChatAdapter(
    private val resources: Resources,
    private val clickAction: ChatAdapterClickAction
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
    ) : BaseViewHolder<ChatInfo>(binding.root) {
        override fun bind(item: ChatInfo, resources: Resources) {
            with(this.binding) {
                if (item.icon == -1) {
                    chatIconIV.setImageDrawable(
                        DrawableController.getDrawableFromId(
                            R.drawable.ic_default_chat,
                            resources
                        )
                    )
                } else {
                    chatIconIV.setImageDrawable(
                        DrawableController.getDrawableFromId(
                            item.icon,
                            resources
                        )
                    )
                }
                chatNameTV.text = item.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ChatInfo> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChatBinding.inflate(inflater, parent, false)
        return ChatsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ChatInfo>, position: Int) {
        holder.bind(chats[position], resources)
        holder.itemView.setOnClickListener {
            clickAction.openChat(chats[position].id)
        }
    }

    override fun getItemCount(): Int = chats.size

    fun updateChats(newChats: List<ChatInfo>) {
        chats = newChats.toMutableList()
    }
}