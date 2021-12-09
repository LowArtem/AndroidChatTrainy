package com.trialbot.trainyapplication.presentation.screen.message

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.databinding.FragmentMessageBinding
import com.trialbot.trainyapplication.domain.contract.HasCustomAppbarIcon
import com.trialbot.trainyapplication.domain.contract.HasCustomTitle
import com.trialbot.trainyapplication.domain.model.MessageDTO
import com.trialbot.trainyapplication.domain.model.UserMessage
import com.trialbot.trainyapplication.presentation.screen.message.recycler.MessageAdapter
import com.trialbot.trainyapplication.presentation.screen.message.recycler.MessageAdapterClickNavigation
import com.trialbot.trainyapplication.presentation.screen.message.recycler.ProfileViewStatus
import com.trialbot.trainyapplication.presentation.screen.profile.ProfileFragment
import com.trialbot.trainyapplication.utils.resultDialog
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class MessageFragment : Fragment(R.layout.fragment_message),
    TextView.OnEditorActionListener, MessageAdapterClickNavigation, HasCustomTitle, HasCustomAppbarIcon {

    private lateinit var binding: FragmentMessageBinding
    private lateinit var adapter: MessageAdapter

    private val args: MessageFragmentArgs by navArgs()

    private val viewModel by viewModel<MessageViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMessageBinding.bind(view)

        val type = viewModel.getUserType(args.chatId)

        adapter = MessageAdapter(
            currentUserId = viewModel.getCurrentUserId(),
            resources = requireContext().resources,
            clickNavigation = this,
            messageItemMenuClick = viewModel,
            isCurrentUserCanDeleteMessages = viewModel.isUserAdminOrCreator(args.chatId, type),
            adminChecking = viewModel
        )

        setHasOptionsMenu(true)

        with (binding)
        {
            val layoutManager = LinearLayoutManager(context)
            messagesRV.layoutManager = layoutManager
            messagesRV.adapter = adapter
            messagesRV.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

            messagesRV.viewTreeObserver.addOnGlobalLayoutListener {
                if (viewModel.needAutoScroll)
                    (adapter.itemCount - 1).takeIf { it > 0 }?.let(messagesRV::smoothScrollToPosition)
                else viewModel.needAutoScroll = true
            }

            sendBtn.setOnClickListener {
                sendMessage()
            }

            messageTextTV.setOnEditorActionListener(this@MessageFragment)
        }

        var avatarId: Int = args.chatIconId
        if (avatarId == -1) avatarId = R.drawable.ic_avatar_default

        setFragmentResultListener(ProfileFragment.TAG_AVATAR_ID_BUNDLE) { _, bundle ->
            avatarId = bundle.getInt(ProfileFragment.TAG_AVATAR_ID_BUNDLE)
            setFragmentResult(
                USER_AVATAR_ICON_TAG,
                bundleOf(USER_AVATAR_ICON_TAG to avatarId)
            )
        }

        viewModel.state.observe(viewLifecycleOwner, { newValue ->
            when(newValue) {
                is MessageState.Loading -> {
                    with(binding) {
                        loadingPanel.visibility = View.VISIBLE
                        textEmpty.visibility = View.GONE
                    }
                }
                is MessageState.Empty -> {
                    Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show()
                    with(binding) {
                        loadingPanel.visibility = View.GONE
                        textEmpty.visibility = View.VISIBLE
                    }
                    viewModel.messages.observe(viewLifecycleOwner, emptyObserver())
                }
                is MessageState.Success -> {
                    with(binding) {
                        loadingPanel.visibility = View.GONE
                        textEmpty.visibility = View.GONE
                    }
                    adapter.updateMessages(newValue.messages)
                    viewModel.messages.observe(viewLifecycleOwner, {
                        if (it != null) {
                            adapter.updateMessages(it)
                        }
                    })
                }
                is MessageState.Error -> {
                    with(binding) {
                        loadingPanel.visibility = View.GONE
                        textEmpty.visibility = View.GONE
                    }
                    Snackbar.make(binding.mainLayout, "Error: ${newValue.errorText}", Snackbar.LENGTH_LONG).show()
                }
            }
        })

        viewModel.isMessageDeleted.observe(viewLifecycleOwner, { result ->
            if (result != null) {
                if (!result) {
                    requireContext().resultDialog(
                        result = false,
                        textFailed = "The message has not been deleted. Maybe you don't have enough rights."
                    )
                    viewModel.clearResult()
                }
            }
        })

        viewModel.isNeedToRedrawRecycler.observe(viewLifecycleOwner, { result ->
            if (result == true) {
                binding.messagesRV.adapter = adapter

                viewModel.clearResult()
            }
        })

        viewModel.render(args.chatId)

        // Observing messages
        viewLifecycleOwner.lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.startMessageObserving()
            }
        }
    }

    private fun emptyObserver(): (t: List<MessageDTO>?) -> Unit =
        {
            if (it != null) {
                adapter.updateMessages(it)
                viewModel.messagesAreNoLongerEmpty(it)
                viewModel.messages.removeObserver(emptyObserver())
            }
        }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chat_profile_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.openChat -> {
                openChatProfile()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onEditorAction(p0: TextView?, actionId: Int, p2: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEND)
        {
            sendMessage()
        }
        return true
    }

    override fun onStop() {
        super.onStop()
        viewModel.applicationClosing()
    }

    private fun sendMessage() {
        with (binding) {
            viewModel.send(messageTextTV.text.toString())
            messageTextTV.text.clear()
        }
    }

    override fun openProfile(user: UserMessage, viewStatus: ProfileViewStatus) {
        val viewStatusStr: String = when (viewStatus) {
            is ProfileViewStatus.Guest -> "guest"
            is ProfileViewStatus.Owner -> "owner"
        }

        val direction = MessageFragmentDirections.actionChatFragmentToProfileFragment(
            viewStatus = viewStatusStr,
            userId = user.id,
            username = user.username,
            userIcon = user.icon,
            currentUsername = viewModel.currentUser!!.username,
            currentUserId = viewModel.currentUser!!.id
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

    private fun openChatProfile() {
        val direction = MessageFragmentDirections.actionMessageFragmentToChatProfileFragment(
            userType = viewModel.getUserType(args.chatId),
            userId = viewModel.getCurrentUserId(),
            chatId = viewModel.chatId!!
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

    override fun getTitle(): String {
        return "  ${args.chatName}"
    }

    override fun getIconRes(): Int {
        return args.chatIconId
    }

    companion object {
        const val USER_AVATAR_ICON_TAG = "user_avatar_icon_tag"
    }
}