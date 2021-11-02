package com.trialbot.trainyapplication.presentation.screen.chat

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
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
import com.trialbot.trainyapplication.databinding.FragmentChatBinding
import com.trialbot.trainyapplication.domain.contract.HasCustomAppbarIcon
import com.trialbot.trainyapplication.domain.contract.HasCustomTitle
import com.trialbot.trainyapplication.domain.model.UserMessage
import com.trialbot.trainyapplication.presentation.recycler.message.MessageAdapter
import com.trialbot.trainyapplication.presentation.recycler.message.MessageAdapterClickNavigation
import com.trialbot.trainyapplication.presentation.recycler.message.ProfileViewStatus
import com.trialbot.trainyapplication.presentation.screen.baseActivity.BaseActivity
import com.trialbot.trainyapplication.presentation.screen.profile.ProfileFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class ChatFragment : Fragment(R.layout.fragment_chat),
    TextView.OnEditorActionListener, MessageAdapterClickNavigation, HasCustomTitle, HasCustomAppbarIcon {

    private lateinit var binding: FragmentChatBinding
    private lateinit var adapter: MessageAdapter

    private val args: ChatFragmentArgs by navArgs()

    private val viewModel by viewModel<ChatViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatBinding.bind(view)

        adapter = MessageAdapter(viewModel.getCurrentUserId(), requireContext().resources, this)

        with (binding)
        {
            val layoutManager = LinearLayoutManager(context)
            messagesRV.layoutManager = layoutManager
            messagesRV.adapter = adapter
            messagesRV.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

            messagesRV.viewTreeObserver.addOnGlobalLayoutListener {
                (adapter.itemCount - 1).takeIf { it > 0 }?.let(messagesRV::smoothScrollToPosition)
            }

            sendBtn.setOnClickListener {
                sendMessage()
            }

            messageTextTV.setOnEditorActionListener(this@ChatFragment)
        }

        var avatarId: Int = args.iconId
        if (avatarId == -1) avatarId = R.drawable.ic_avatar_default

        setFragmentResultListener(ProfileFragment.TAG_AVATAR_ID_BUNDLE) { _, bundle ->
            avatarId = bundle.getInt(ProfileFragment.TAG_AVATAR_ID_BUNDLE)

            (requireActivity() as BaseActivity).updateActionBarIcon(avatarId)
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

        viewModel.render()

        // Observing messages
        viewLifecycleOwner.lifecycleScope.launch{
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.startMessageObserving()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.applicationStarting()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_exit -> {
                actionExitHandler()
                true
            }
            R.id.action_logout -> {
                actionLogoutHandler()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun actionLogoutHandler() {
        viewModel.logOut()

        findNavController().navigate(ChatFragmentDirections.actionChatFragmentToLoginFragment(), navOptions {
            anim {
                enter = R.anim.enter
                exit = R.anim.exit
                popEnter = R.anim.pop_enter
                popExit = R.anim.pop_exit
            }
        })
    }

    private fun actionExitHandler() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Confirmation")
            setMessage("Are you sure, you want to exit?")

            setPositiveButton("Yes") { _, _ ->
                requireActivity().onBackPressed()
            }

            setNegativeButton("Cancel") { _, _ -> }
            setCancelable(true)
        }.create().show()
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
            is ProfileViewStatus.Guest -> {
                "guest"
            }
            is ProfileViewStatus.Owner -> {
                "owner"
            }
        }

        val direction = ChatFragmentDirections.actionChatFragmentToProfileFragment(viewStatusStr, user.id, user.username, user.icon)
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
        return "  ${args.username}"
    }

    override fun getIconRes(): Int {
        return args.iconId
    }
}