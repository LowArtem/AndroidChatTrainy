package com.trialbot.trainyapplication.presentation.screen.profile.recycler

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.trialbot.trainyapplication.databinding.ItemAvatarBinding
import com.trialbot.trainyapplication.domain.model.AvatarItem
import com.trialbot.trainyapplication.presentation.drawable.DrawableController

interface AvatarAdapterClickAction {
    fun changeAvatar(@DrawableRes avatarId: Int)
}

class AvatarAdapter(
    private val resources: Resources,
    private val clickAction: AvatarAdapterClickAction
) : RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    private var avatars: MutableList<AvatarItem> = mutableListOf()

    class AvatarViewHolder(
        private val binding: ItemAvatarBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(@DrawableRes avatarItem: Int, avatarName: String, resources: Resources) {
            with(this.binding) {
                avatarImageIV.setImageDrawable(DrawableController.getDrawableFromId(avatarItem, resources))
                avatarNameTV.text = avatarName
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAvatarBinding.inflate(inflater, parent, false)
        return AvatarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        holder.bind(avatars[position].avatarIcon, avatars[position].avatarName, resources)

        holder.itemView.setOnClickListener {
            clickAction.changeAvatar(avatars[position].avatarIcon)
        }
    }

    override fun getItemCount(): Int = avatars.size

    fun setAvatarList(list: List<AvatarItem>) {
        avatars = list.toMutableList()
        notifyDataSetChanged()
    }
}