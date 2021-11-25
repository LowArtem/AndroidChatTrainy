package com.trialbot.trainyapplication.presentation.screen.createChat.recycler

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trialbot.trainyapplication.databinding.ItemMemberBinding
import com.trialbot.trainyapplication.domain.model.UserWithoutPassword
import com.trialbot.trainyapplication.presentation.drawable.DrawableController
import com.trialbot.trainyapplication.utils.BaseViewHolder

interface UserSearchAdapterClickAction {
    fun createDialog(selectedUserId: Long)
}

class UserSearchAdapter(
    private val resources: Resources,
    private val userSearchAdapterClickAction: UserSearchAdapterClickAction
): RecyclerView.Adapter<BaseViewHolder<UserWithoutPassword>>() {

    private var searchedUsers: MutableList<UserWithoutPassword> = mutableListOf()
        set(newValue) {
            field.clear()
            field = newValue
            notifyDataSetChanged()
        }

    class UserSearchViewHolder(
        private var binding: ItemMemberBinding
    ): BaseViewHolder<UserWithoutPassword>(binding.root) {
        override fun bind(item: UserWithoutPassword, resources: Resources) {
            with(binding) {
                memberIconIV.setImageDrawable(DrawableController.getDrawableFromId(item.icon, resources))
                memberNameTV.text = item.username

                deleteUserBtn.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<UserWithoutPassword> {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMemberBinding.inflate(inflater, parent, false)
        return UserSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<UserWithoutPassword>, position: Int) {
        holder.bind(searchedUsers[position], resources)

        holder.itemView.setOnClickListener {
            userSearchAdapterClickAction.createDialog(searchedUsers[position].id)
        }
    }

    override fun getItemCount(): Int = searchedUsers.size

    fun updateSearchedUsers(newUsers: List<UserWithoutPassword>) {
        if (!searchedUsers.containsAll(newUsers) || !newUsers.containsAll(searchedUsers))
            searchedUsers = newUsers.toMutableList()
    }
}