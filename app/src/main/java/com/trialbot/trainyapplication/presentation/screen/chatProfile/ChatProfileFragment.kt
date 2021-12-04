package com.trialbot.trainyapplication.presentation.screen.chatProfile

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.databinding.FragmentChatProfileBinding
import com.trialbot.trainyapplication.domain.contract.HasCustomAppbarIcon
import com.trialbot.trainyapplication.domain.contract.HasCustomTitle
import com.trialbot.trainyapplication.presentation.screen.chatProfile.recycler.MembersAdapter
import com.trialbot.trainyapplication.utils.confirmDialog
import com.trialbot.trainyapplication.utils.resultDialog
import com.trialbot.trainyapplication.utils.resultDialogWithoutSuccessText
import org.koin.androidx.viewmodel.ext.android.viewModel


class ChatProfileFragment : Fragment(R.layout.fragment_chat_profile), HasCustomTitle, HasCustomAppbarIcon {

    enum class CoverMode {
        AVATAR,
        MEMBER_OPTIONS
    }

    private lateinit var binding: FragmentChatProfileBinding
    private lateinit var adapter: MembersAdapter

    private val viewModel by viewModel<ChatProfileViewModel>()
    private val args: ChatProfileFragmentArgs by navArgs()

    private var coverMode: CoverMode = CoverMode.AVATAR

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatProfileBinding.bind(view)

        viewModel.render(args.userType, args.userId, args.chatId)

        viewModel.state.observe(viewLifecycleOwner, {state ->
            with(binding) {
                when (state) {
                    is ChatProfileState.Loading -> {
                        deleteChatBtn.visibility = View.GONE
                        deleteAdminBtn.visibility = View.GONE
                        addAdminBtn.visibility = View.GONE
                        loadingPanel.visibility = View.VISIBLE
                        errorLayout.visibility = View.GONE
                    }
                    is ChatProfileState.Creator -> {
                        deleteChatBtn.visibility = View.VISIBLE
                        deleteChatBtn.text = getString(R.string.delete_chat_btn)

                        if (state.isDialog) {
                            deleteAdminBtn.visibility = View.GONE
                            addAdminBtn.visibility = View.GONE
                            aboutLL.visibility = View.GONE
                            membersRV.visibility = View.GONE
                        } else {
                            deleteAdminBtn.visibility = View.VISIBLE
                            addAdminBtn.visibility = View.VISIBLE
                            aboutLL.visibility = View.VISIBLE
                            membersRV.visibility = View.VISIBLE
                        }

                        loadingPanel.visibility = View.GONE
                        errorLayout.visibility = View.GONE

                        adapter = MembersAdapter(
                            resources = resources,
                            membersAdapterClickListener = viewModel,
                            getMemberType = viewModel
                        )

                        setDefaultChatData(state.chatName, state.chatIcon)

                        deleteChatBtn.setOnClickListener {
                            requireContext().confirmDialog("Are you sure you want to permanently delete this chat?") {
                                viewModel.deleteChat()
                            }
                        }

                    }
                    is ChatProfileState.Admin -> {
                        deleteChatBtn.visibility = View.VISIBLE
                        deleteChatBtn.text = getString(R.string.leave_chat_btn)

                        deleteAdminBtn.visibility = View.GONE
                        addAdminBtn.visibility = View.GONE
                        loadingPanel.visibility = View.GONE
                        errorLayout.visibility = View.GONE

                        adapter = MembersAdapter(
                            resources = resources,
                            membersAdapterClickListener = viewModel,
                            getMemberType = viewModel
                        )

                        setDefaultChatData(state.chatName, state.chatIcon)

                        deleteChatBtn.setOnClickListener {
                            requireContext().confirmDialog("Are you sure you want to leave this chat?") {
                                viewModel.leaveChat()
                            }
                        }
                    }
                    is ChatProfileState.Member -> {
                        deleteChatBtn.visibility = View.VISIBLE
                        deleteChatBtn.text = getString(R.string.leave_chat_btn)

                        deleteAdminBtn.visibility = View.GONE
                        addAdminBtn.visibility = View.GONE
                        loadingPanel.visibility = View.GONE
                        errorLayout.visibility = View.GONE

                        adapter = MembersAdapter(
                            resources = resources,
                            membersAdapterClickListener = viewModel,
                            getMemberType = viewModel
                        )

                        setDefaultChatData(state.chatName, state.chatIcon)

                        deleteChatBtn.setOnClickListener {
                            requireContext().confirmDialog("Are you sure you want to leave this chat?") {
                                viewModel.leaveChat()
                            }
                        }
                    }
                    is ChatProfileState.Error -> {
                        deleteChatBtn.visibility = View.GONE
                        deleteAdminBtn.visibility = View.GONE
                        addAdminBtn.visibility = View.GONE
                        loadingPanel.visibility = View.GONE

                        errorLayout.visibility = View.VISIBLE
                    }
                }
            }
        })

        with(binding) {
            viewModel.isChatDeleted.observe(viewLifecycleOwner, { result ->
                if (result != null) {
                    requireContext().resultDialog(
                        result = result,
                        textSuccess = "The chat was successfully deleted",
                        textFailed = "The chat has not been deleted. Maybe you don't have enough rights.",
                        successfulAction = {
                            val direction = ChatProfileFragmentDirections.actionChatProfileFragmentToChatFragment(
                                username = viewModel.getLocalUser()?.username ?: "",
                                iconId = viewModel.getLocalUser()?.icon ?: -1,
                                userId = viewModel.currentUserId
                            )
                            findNavController().navigate(direction, navOptions {
                                anim {
                                    enter = R.anim.enter
                                    exit = R.anim.exit
                                    popEnter = R.anim.pop_enter
                                    popExit = R.anim.pop_exit
                                }
                            })
                        }
                    )
                }
            })

            viewModel.isAdminAdded.observe(viewLifecycleOwner, { result ->
                if (result != null) {
                    if (result) {
                        setFragmentResult(
                            ADMIN_UPDATED_TAG,
                            bundleOf(ADMIN_UPDATED_TAG to true)
                        )
                    }

                    requireContext().resultDialogWithoutSuccessText(
                        result = result,
                        textFailed = "Admin has not been added. We are working on a fix."
                    )
                }
            })

            viewModel.isAdminDeleted.observe(viewLifecycleOwner, { result ->
                if (result != null) {
                    if (result) {
                        setFragmentResult(
                            ADMIN_UPDATED_TAG,
                            bundleOf(ADMIN_UPDATED_TAG to true)
                        )
                    }

                    requireContext().resultDialogWithoutSuccessText(
                        result = result,
                        textFailed = "Admin has not been deleted. We are working on a fix."
                    )
                }
            })

            viewModel.isMemberDeleted.observe(viewLifecycleOwner, { result ->
                if (result != null) {
                    requireContext().resultDialog(
                        result = result,
                        textSuccess = "Member was successfully deleted",
                        textFailed = "Member has not been deleted. We are working on a fix."
                    )
                }
            })

            viewModel.isChatLeft.observe(viewLifecycleOwner, { result ->
                if (result != null) {
                    requireContext().resultDialogWithoutSuccessText(
                        result = result,
                        textFailed = "Something went wrong. We are working on a fix.",
                        successfulAction = {
                            val direction = ChatProfileFragmentDirections.actionChatProfileFragmentToChatFragment(
                                username = viewModel.getLocalUser()?.username ?: "",
                                iconId = viewModel.getLocalUser()?.icon ?: -1,
                                userId = viewModel.currentUserId
                            )
                            findNavController().navigate(direction, navOptions {
                                anim {
                                    enter = R.anim.enter
                                    exit = R.anim.exit
                                    popEnter = R.anim.pop_enter
                                    popExit = R.anim.pop_exit
                                }
                            })
                        }
                    )
                }
            })

            viewModel.selectedUserId.observe(viewLifecycleOwner, { selectedUserId ->
                if (selectedUserId == null) {
                    chatCoverTransparent.visibility = View.GONE
                    memberOptionsLL.visibility = View.GONE
                } else {
                    when(viewModel.getMemberType(viewModel.currentUserId)) {
                        UserType.Creator -> {
                            coverMode = CoverMode.MEMBER_OPTIONS
                            chatCoverTransparent.visibility = View.VISIBLE

                            if (viewModel.getMemberType(viewModel.selectedUserId.value ?: -1) != UserType.Admin)
                                deleteMemberBtn.visibility = View.VISIBLE
                            else deleteMemberBtn.visibility = View.GONE

                            if (viewModel.getMemberType(viewModel.selectedUserId.value ?: -1) == UserType.Admin)
                                deleteAdminBtn.visibility = View.VISIBLE
                            else deleteAdminBtn.visibility = View.GONE

                            if (viewModel.getMemberType(viewModel.selectedUserId.value ?: -1) != UserType.Admin)
                                addAdminBtn.visibility = View.VISIBLE
                            else addAdminBtn.visibility = View.GONE

                            memberOptionsLL.visibility = View.VISIBLE
                        }
                        UserType.Admin -> {
                            if (viewModel.getMemberType(viewModel.selectedUserId.value ?: -1) == UserType.Member) {
                                coverMode = CoverMode.MEMBER_OPTIONS
                                chatCoverTransparent.visibility = View.VISIBLE

                                deleteMemberBtn.visibility = View.VISIBLE
                                deleteAdminBtn.visibility = View.GONE
                                addAdminBtn.visibility = View.GONE

                                memberOptionsLL.visibility = View.VISIBLE
                            }
                        }
                        UserType.Member -> {
                            memberOptionsLL.visibility = View.GONE
                        }
                    }
                }
            })

            addAdminBtn.setOnClickListener {
                viewModel.addAdmin()
            }

            deleteAdminBtn.setOnClickListener {
                viewModel.deleteAdmin()
            }

            deleteMemberBtn.setOnClickListener {
                viewModel.deleteMember()
            }

            chatCoverTransparent.setOnClickListener {
                when(coverMode) {
                    CoverMode.AVATAR -> {
                        // TODO: реализовать изменение аватара
                    }
                    CoverMode.MEMBER_OPTIONS -> {
                        viewModel.unselectMember()
                    }
                }
            }
        }
    }

    private fun setDefaultChatData(chatName: String, chatIcon: Int) {
        with(binding) {
            chatNameTV.text = chatName

            if (chatIcon != -1)
                chatIconIV.setImageResource(chatIcon)
            else
                chatIconIV.setImageResource(R.drawable.ic_default_chat)

            // Adapter setting
            val layoutManager = LinearLayoutManager(requireContext())
            membersRV.layoutManager = layoutManager
            membersRV.adapter = adapter
            membersRV.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

            viewModel.chatMembers.observe(viewLifecycleOwner, { members ->
                if (members != null) {
                    adapter.updateMembers(members)
                }
            })

            viewModel.membersCount.observe(viewLifecycleOwner, { count ->
                updateMembersCount(count)
            })
        }
    }

    private fun updateMembersCount(newCount: Int) {
        binding.membersCount.text = if (newCount <= 1) "$newCount member" else "$newCount members"
    }

    override fun getIconRes(): Int? {
        return null
    }

    override fun getTitle(): String {
        return "Chat info"
    }

    companion object {
        const val ADMIN_UPDATED_TAG = "ADMIN_UPDATED_TAG"
    }
}