package com.trialbot.trainyapplication.presentation.screen.chatProfile.recycler

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trialbot.trainyapplication.databinding.ItemMemberAdminBinding
import com.trialbot.trainyapplication.databinding.ItemMemberBinding
import com.trialbot.trainyapplication.databinding.ItemMemberCreatorBinding
import com.trialbot.trainyapplication.domain.model.UserWithoutPassword
import com.trialbot.trainyapplication.presentation.drawable.DrawableController
import com.trialbot.trainyapplication.utils.BaseViewHolder

interface MembersAdapterClickListener {
    fun memberSelected(selectedUserId: Long)
}

interface GetMemberType {
    fun getMemberType(memberId: Long): MemberType
}

class MembersAdapter(
    private val resources: Resources,
    private val membersAdapterClickListener: MembersAdapterClickListener,
    private val getMemberType: GetMemberType
): RecyclerView.Adapter<BaseViewHolder<UserWithoutPassword>>() {

    private var members: MutableList<UserWithoutPassword> = mutableListOf()
        set(newValue) {
            field.clear()
            field = newValue
            notifyDataSetChanged()
        }

    private var selectedPos = RecyclerView.NO_POSITION

    class MembersViewHolder(
        private val binding: ItemMemberBinding
    ): BaseViewHolder<UserWithoutPassword>(binding.root) {
        override fun bind(item: UserWithoutPassword, resources: Resources) {
            with(binding) {
                memberIconIV.setImageDrawable(DrawableController.getDrawableFromId(item.icon, resources))
                memberNameTV.text = item.username
            }
        }

    }

    class AdminsViewHolder(
        private val binding: ItemMemberAdminBinding,
    ): BaseViewHolder<UserWithoutPassword>(binding.root) {
        override fun bind(item: UserWithoutPassword, resources: Resources) {
            with(binding) {
                memberIconIV.setImageDrawable(DrawableController.getDrawableFromId(item.icon, resources))
                memberNameTV.text = item.username
            }
        }
    }

    class CreatorsViewHolder(
        private val binding: ItemMemberCreatorBinding
    ): BaseViewHolder<UserWithoutPassword>(binding.root) {
        override fun bind(item: UserWithoutPassword, resources: Resources) {
            with(binding) {
                memberIconIV.setImageDrawable(DrawableController.getDrawableFromId(item.icon, resources))
                memberNameTV.text = item.username
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<UserWithoutPassword> {
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            TYPE_MEMBER -> {
                val binding = ItemMemberBinding.inflate(inflater, parent, false)
                return MembersViewHolder(binding)
            }
            TYPE_ADMIN -> {
                val binding = ItemMemberAdminBinding.inflate(inflater, parent, false)
                return AdminsViewHolder(binding)
            }
            TYPE_CREATOR -> {
                val binding = ItemMemberCreatorBinding.inflate(inflater, parent, false)
                return CreatorsViewHolder(binding)
            }
            else -> { // Equals to TYPE_MEMBER
                val binding = ItemMemberBinding.inflate(inflater, parent, false)
                return MembersViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<UserWithoutPassword>, position: Int) {
        when(holder) {
            is MembersViewHolder -> holder.bind(members[position], resources)
            is AdminsViewHolder -> holder.bind(members[position], resources)
            is CreatorsViewHolder -> holder.bind(members[position], resources)
        }
        holder.itemView.setOnClickListener {
            if (holder !is CreatorsViewHolder) {
                membersAdapterClickListener.memberSelected(members[position].id)
            }
        }
    }

    override fun getItemCount(): Int = members.size

    override fun getItemViewType(position: Int): Int {
        val memberType = getMemberType.getMemberType(members[position].id)
        return when(memberType) {
            MemberType.COMMON_MEMBER -> TYPE_MEMBER
            MemberType.ADMIN -> TYPE_ADMIN
            MemberType.CREATOR -> TYPE_CREATOR
        }
    }

    fun updateMembers(new_members: List<UserWithoutPassword>) {
        members = new_members.toMutableList()
    }

    companion object {
        private const val TYPE_MEMBER = 1
        private const val TYPE_ADMIN = 2
        private const val TYPE_CREATOR = 3
    }
}