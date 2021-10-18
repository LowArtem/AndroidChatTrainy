package com.trialbot.trainyapplication.presentation.recycler.message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.trialbot.trainyapplication.data.model.MessageDTO
import com.trialbot.trainyapplication.databinding.ItemMessageBinding
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
        return oldMessages[oldItemPosition] == newMessages[newItemPosition]
    }
}


class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private var messages: MutableList<MessageDTO> = mutableListOf()
        set(newValue) {
            val diffCallback = MessageDiffCallback(field, newValue)
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            field.clear()
            field = newValue
            diffResult.dispatchUpdatesTo(this)
        }

    class MessageViewHolder(val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMessageBinding.inflate(inflater, parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        with(holder.binding) {
            // TODO: добавить получение аватара
            authorNameTV.text = messages[position].author.username
            messageTextTV.text = messages[position].text

            val formatter = SimpleDateFormat("HH:mm, dd.MM.yyyy", Locale.ROOT)
            pubDateTV.text = formatter.format(messages[position].pubDate)
        }
    }

    override fun getItemCount(): Int = messages.size

    fun updateMessages(new_messages: List<MessageDTO>) {
        messages = new_messages.toMutableList()
    }
}