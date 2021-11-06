package com.trialbot.trainyapplication.presentation.screen.message.recycler

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.trialbot.trainyapplication.databinding.ItemMessageBinding
import com.trialbot.trainyapplication.databinding.ItemMyMessageBinding
import com.trialbot.trainyapplication.domain.model.MessageDTO
import com.trialbot.trainyapplication.domain.model.UserMessage
import com.trialbot.trainyapplication.presentation.drawable.DrawableController
import java.text.SimpleDateFormat
import java.util.*


class MessageDiffCallback(
    private val oldMessages: List<MessageDTO>,
    private val newMessages: List<MessageDTO>
): DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldMessages.size

    override fun getNewListSize(): Int = newMessages.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldMessages[oldItemPosition].pubDate == newMessages[newItemPosition].pubDate &&
                oldMessages[oldItemPosition].author.id == newMessages[newItemPosition].author.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldMessages[oldItemPosition] == newMessages[newItemPosition] &&
                oldMessages[oldItemPosition].author.icon == newMessages[newItemPosition].author.icon
    }
}


abstract class BaseViewHolder<T>(viewItem: View) : RecyclerView.ViewHolder(viewItem) {
    abstract fun bind(item: T, resources: Resources)
}

sealed class ProfileViewStatus {
    object Guest: ProfileViewStatus()
    object Owner: ProfileViewStatus()
}

interface MessageAdapterClickNavigation {
    fun openProfile(user: UserMessage, viewStatus: ProfileViewStatus)
}

class MessageAdapter(
    private val currentUserId: Long,
    private val resources: Resources,
    private val clickNavigation: MessageAdapterClickNavigation
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    private var messages: MutableList<MessageDTO> = mutableListOf()
        set(newValue) {
            val diffCallback = MessageDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field.clear()
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    class CommonMessageViewHolder(
        private val binding: ItemMessageBinding,
        private val clickNavigation: MessageAdapterClickNavigation
    ) : BaseViewHolder<MessageDTO>(binding.root) {
        override fun bind(item: MessageDTO, resources: Resources) {
            with(this.binding) {
                authorAvatarIV.setImageDrawable(DrawableController.getDrawableFromId(item.author.icon, resources))
                authorNameTV.text = item.author.username
                messageTextTV.text = item.text

                val formatter = SimpleDateFormat("HH:mm, dd.MM.yyyy", Locale.ROOT)
                pubDateTV.text = formatter.format(item.pubDate)

                authorAvatarIV.setOnClickListener {
                    clickNavigation.openProfile(item.author, ProfileViewStatus.Guest)
                }
            }
        }
    }

    class MyMessageViewHolder(
        private val binding: ItemMyMessageBinding,
        private val clickNavigation: MessageAdapterClickNavigation
    ) : BaseViewHolder<MessageDTO>(binding.root) {
        override fun bind(item: MessageDTO, resources: Resources) {
            with(this.binding) {
                authorAvatarIV.setImageDrawable(DrawableController.getDrawableFromId(item.author.icon, resources))
                authorNameTV.text = item.author.username
                messageTextTV.text = item.text

                val formatter = SimpleDateFormat("HH:mm, dd.MM.yyyy", Locale.ROOT)
                pubDateTV.text = formatter.format(item.pubDate)

                authorAvatarIV.setOnClickListener {
                    clickNavigation.openProfile(item.author, ProfileViewStatus.Owner)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (messages[position].author.id == currentUserId) return TYPE_MY_MESSAGE
        else return TYPE_COMMON_MESSAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == TYPE_MY_MESSAGE) {
            val binding = ItemMyMessageBinding.inflate(inflater, parent, false)
            return MyMessageViewHolder(binding, clickNavigation)
        }
        else {
            val binding = ItemMessageBinding.inflate(inflater, parent, false)
            return CommonMessageViewHolder(binding, clickNavigation)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when(holder) {
            is CommonMessageViewHolder -> holder.bind(messages[position], resources)
            is MyMessageViewHolder -> holder.bind(messages[position], resources)
        }
    }

    override fun getItemCount(): Int = messages.size

    fun updateMessages(new_messages: List<MessageDTO>) {
        messages = new_messages.toMutableList()
    }

    companion object {
        private const val TYPE_COMMON_MESSAGE = 0
        private const val TYPE_MY_MESSAGE = 1
    }
}