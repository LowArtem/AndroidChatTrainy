package com.trialbot.trainyapplication.presentation.recycler.message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trialbot.trainyapplication.data.model.MessageDTO
import com.trialbot.trainyapplication.databinding.ItemMessageBinding
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val messages: MutableList<MessageDTO> = mutableListOf()

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
        if (messages.isNotEmpty() && new_messages.isNotEmpty()) {
            if (messages.last() != new_messages.last()) {
                messages.clear()
                messages.addAll(new_messages)

                if (messages.size > 10) {
                    notifyItemRangeChanged(messages.size - 10, 10)
                } else {
                    notifyDataSetChanged()
                }
            }
        }
        else {
            messages.addAll(new_messages)
            notifyDataSetChanged()
        }
    }
}