package com.trialbot.trainyapplication.presentation

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.data.model.UserMessage
import com.trialbot.trainyapplication.databinding.FragmentMainBinding
import com.trialbot.trainyapplication.presentation.recycler.message.MessageAdapter
import com.trialbot.trainyapplication.presentation.recycler.message.MessageAdapterClickNavigation
import com.trialbot.trainyapplication.presentation.recycler.message.ProfileViewStatus
import com.trialbot.trainyapplication.presentation.state.MessageState
import com.trialbot.trainyapplication.presentation.viewmodel.MainViewModel




class MainFragment : Fragment(R.layout.fragment_main), TextView.OnEditorActionListener, MessageAdapterClickNavigation {

    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: MessageAdapter

    private val args: MainFragmentArgs by navArgs()

    private val viewModel: MainViewModel by viewModels {
        val prefs = requireActivity().getSharedPreferences(MyApp.SHARED_PREFS_AUTH_TAG, Context.MODE_PRIVATE) ?:
        throw Exception("Shared Preferences is null")

        MainViewModel.MainViewModelFactory(
            chatApi = (requireActivity().application as MyApp).api,
            sharedPrefs = prefs
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMainBinding.bind(view)

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

            messageTextTV.setOnEditorActionListener(this@MainFragment)
        }

        requireActivity().actionBar?.setDisplayUseLogoEnabled(true)
        requireActivity().actionBar?.setDisplayShowHomeEnabled(true)

        var avatarId: Int = args.iconId
        if (avatarId == -1) avatarId = R.drawable.ic_avatar_default

        viewModel.setUserIconId(avatarId)

        requireActivity().actionBar?.setLogo(avatarId)

        viewModel.state.observe(viewLifecycleOwner, { newValue ->
            when(newValue) {
                is MessageState.Loading -> {
                    with(binding) {
                        loadingPanel.visibility = View.VISIBLE
                        textEmpty.visibility = View.GONE
                    }
                    Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show()
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
                        adapter.updateMessages(it)
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
    }

    override fun onStart() {
        super.onStart()
        viewModel.render()
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

        findNavController().navigate(MainFragmentDirections.actionMainFragmentToLoginFragment(), navOptions {
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
                findNavController().popBackStack()
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
        viewModel.stopMessageObserving()
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

        val direction = MainFragmentDirections.actionMainFragmentToProfileFragment(viewStatusStr, user.id, user.username, user.icon)
        findNavController().navigate(direction, navOptions {
            anim {
                enter = R.anim.enter
                exit = R.anim.exit
                popEnter = R.anim.pop_enter
                popExit = R.anim.pop_exit
            }
        })
    }
}