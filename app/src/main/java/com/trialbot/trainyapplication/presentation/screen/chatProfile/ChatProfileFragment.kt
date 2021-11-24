package com.trialbot.trainyapplication.presentation.screen.chatProfile

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
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
import org.koin.androidx.viewmodel.ext.android.viewModel


class ChatProfileFragment : Fragment(R.layout.fragment_chat_profile), HasCustomTitle, HasCustomAppbarIcon {

    private lateinit var binding: FragmentChatProfileBinding
    private lateinit var adapter: MembersAdapter

    private val viewModel by viewModel<ChatProfileViewModel>()
    private val args: ChatProfileFragmentArgs by navArgs()

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
                    }
                    is ChatProfileState.Admin -> {
                        deleteChatBtn.visibility = View.GONE
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
                    }
                    is ChatProfileState.Member -> {
                        deleteChatBtn.visibility = View.GONE
                        deleteAdminBtn.visibility = View.GONE
                        addAdminBtn.visibility = View.GONE
                        loadingPanel.visibility = View.GONE
                        errorLayout.visibility = View.GONE

                        adapter = MembersAdapter(
                            resources = resources,
                            membersAdapterClickListener = viewModel,
                            getMemberType = viewModel,
                            isDeleteBtnVisible = false
                        )

                        setDefaultChatData(state.chatName, state.chatIcon)
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
            deleteChatBtn.setOnClickListener {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle("Confirmation")
                    setMessage("Are you sure you want to permanently delete this chat?")

                    setPositiveButton("Yes") { _, _ ->
                        viewModel.deleteChat()
                    }
                    setNegativeButton("Cancel") {_, _ -> }

                    setCancelable(true)
                }.create().show()
            }
            viewModel.isChatDeleted.observe(viewLifecycleOwner, { result ->
                if (result != null) {
                    if (result) {
                        AlertDialog.Builder(requireContext()).apply {
                            setTitle("Chat deleted")
                            setMessage("The chat was successfully deleted")

                            setNeutralButton("Ok") { _, _ ->
                                val direction = ChatProfileFragmentDirections.actionChatProfileFragmentToChatFragment(
                                    username = viewModel.getLocalUser()?.username ?: "",
                                    iconId = viewModel.getLocalUser()?.icon ?: -1,
                                    userId = viewModel.userId
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

                            setCancelable(false)
                        }.create().show()

                    } else {
                        AlertDialog.Builder(requireContext()).apply {
                            setTitle("The deleting was failed")
                            setMessage("The chat has not been deleted. Maybe you don't have enough rights.")

                            setNeutralButton("Ok") { _, _ -> }

                            setCancelable(true)
                        }.create().show()
                    }
                }
            })
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
}