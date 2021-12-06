package com.trialbot.trainyapplication.presentation.screen.message.recycler

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.databinding.ItemMessageBinding
import com.trialbot.trainyapplication.databinding.ItemMyMessageBinding
import com.trialbot.trainyapplication.domain.model.MessageDTO
import com.trialbot.trainyapplication.domain.model.UserMessage
import com.trialbot.trainyapplication.presentation.drawable.DrawableController
import java.text.SimpleDateFormat
import java.util.*


private object MessageDiffCallback : DiffUtil.ItemCallback<MessageDTO>() {
    override fun areItemsTheSame(oldItem: MessageDTO, newItem: MessageDTO): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: MessageDTO, newItem: MessageDTO): Boolean {
        return oldItem.author.icon == newItem.author.icon && oldItem.text == newItem.text
    }
}

sealed class ProfileViewStatus {
    object Guest: ProfileViewStatus()
    object Owner: ProfileViewStatus()
}

interface MessageAdapterClickNavigation {
    fun openProfile(user: UserMessage, viewStatus: ProfileViewStatus)
}

interface MessageItemMenuClick {
    fun executeMessageMenuItemAction(messageId: Long, menuOption: MessageItemMenuOptions)
}

interface AdminChecking {
    fun isUserAdmin(userId: Long): Boolean
}

abstract class BasePagingViewHolder<T>(viewItem: View) : RecyclerView.ViewHolder(viewItem) {
    abstract fun bind(item: T?, resources: Resources)
}

class MessageAdapter(
    private val currentUserId: Long,
    private val resources: Resources,
    private val clickNavigation: MessageAdapterClickNavigation,
    private val messageItemMenuClick: MessageItemMenuClick,
    private val isCurrentUserCanDeleteMessages: Boolean,
    private val adminChecking: AdminChecking
) : PagingDataAdapter<MessageDTO, BasePagingViewHolder<*>>(MessageDiffCallback) {

    class CommonMessageViewHolder(
        private val binding: ItemMessageBinding,
        private val clickNavigation: MessageAdapterClickNavigation,
        private val messageItemMenuClick: MessageItemMenuClick,
        private val isCurrentUserCanDeleteMessages: Boolean,
        private val adminChecking: AdminChecking
    ) : BasePagingViewHolder<MessageDTO>(binding.root) {

        override fun bind(item: MessageDTO?, resources: Resources) {
            with(this.binding) {
                if (item != null) {
                    authorAvatarIV.setImageDrawable(
                        DrawableController.getDrawableFromId(
                            item.author.icon,
                            resources
                        )
                    )
                    authorNameTV.visibility = View.VISIBLE
                    authorNameTV.text = item.author.username

                    if (adminChecking.isUserAdmin(item.author.id)) {
                        authorNameTV.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_shield,
                            0
                        )
                    } else {
                        authorNameTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    }

                    messageTextTV.visibility = View.VISIBLE
                    messageTextTV.text = item.text

                    val formatter = SimpleDateFormat("HH:mm, dd.MM.yyyy", Locale.ROOT)
                    pubDateTV.text = formatter.format(item.pubDate)

                    authorAvatarIV.setOnClickListener {
                        clickNavigation.openProfile(item.author, ProfileViewStatus.Guest)
                    }

                    moreBtn.visibility =
                        if (isCurrentUserCanDeleteMessages) View.VISIBLE else View.GONE

                    moreBtn.setOnClickListener {
                        val popupMenu = PopupMenu(itemView.context, moreBtn)
                        popupMenu.inflate(R.menu.message_menu)
                        popupMenu.setOnMenuItemClickListener { menuItem ->
                            when (menuItem.itemId) {
                                R.id.deleteMessageBtn -> {
                                    messageItemMenuClick.executeMessageMenuItemAction(
                                        item.id,
                                        MessageItemMenuOptions.DELETE
                                    )
                                    true
                                }
                                else -> {
                                    false
                                }
                            }
                        }
                        popupMenu.show()
                    }
                } else {
                    authorAvatarIV.setImageResource(R.drawable.ic_avatar_skeleton)
                    authorNameTV.visibility = View.GONE
                    authorNameSkeleton.visibility = View.VISIBLE
                    messageTextTV.visibility = View.GONE
                    messageTextSkeleton.visibility = View.VISIBLE
                }
            }
        }
    }

    class MyMessageViewHolder(
        private val binding: ItemMyMessageBinding,
        private val clickNavigation: MessageAdapterClickNavigation,
        private val adminChecking: AdminChecking
    ) : BasePagingViewHolder<MessageDTO>(binding.root) {
        override fun bind(item: MessageDTO?, resources: Resources) {
            with(this.binding) {
                if (item != null) {
                    authorAvatarIV.setImageDrawable(
                        DrawableController.getDrawableFromId(
                            item.author.icon,
                            resources
                        )
                    )
                    authorNameTV.visibility = View.VISIBLE
                    authorNameTV.text = item.author.username

                    if (adminChecking.isUserAdmin(item.author.id)) {
                        authorNameTV.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_shield,
                            0
                        )
                    } else {
                        authorNameTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    }

                    messageTextTV.visibility = View.VISIBLE
                    messageTextTV.text = item.text

                    val formatter = SimpleDateFormat("HH:mm, dd.MM.yyyy", Locale.ROOT)
                    pubDateTV.text = formatter.format(item.pubDate)

                    authorAvatarIV.setOnClickListener {
                        clickNavigation.openProfile(item.author, ProfileViewStatus.Owner)
                    }
                } else {
                    authorAvatarIV.setImageResource(R.drawable.ic_avatar_skeleton)
                    authorNameTV.visibility = View.GONE
                    authorNameSkeleton.visibility = View.VISIBLE
                    messageTextTV.visibility = View.GONE
                    messageTextSkeleton.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (getItem(position)?.author?.id == currentUserId) return TYPE_MY_MESSAGE
        else return TYPE_COMMON_MESSAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasePagingViewHolder<*> {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == TYPE_MY_MESSAGE) {
            val binding = ItemMyMessageBinding.inflate(inflater, parent, false)
            return MyMessageViewHolder(
                binding = binding,
                clickNavigation = clickNavigation,
                adminChecking = adminChecking
            )
        }
        else {
            val binding = ItemMessageBinding.inflate(inflater, parent, false)
            return CommonMessageViewHolder(
                binding = binding,
                clickNavigation = clickNavigation,
                messageItemMenuClick = messageItemMenuClick,
                isCurrentUserCanDeleteMessages = isCurrentUserCanDeleteMessages,
                adminChecking = adminChecking
            )
        }
    }

    override fun onBindViewHolder(holder: BasePagingViewHolder<*>, position: Int) {
        when(holder) {
            is CommonMessageViewHolder -> holder.bind(getItem(position), resources)
            is MyMessageViewHolder -> holder.bind(getItem(position), resources)
        }
    }

    companion object {
        private const val TYPE_COMMON_MESSAGE = 0
        private const val TYPE_MY_MESSAGE = 1
    }
}