package com.trialbot.trainyapplication.presentation.screen.chooseChat

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.google.android.material.snackbar.Snackbar
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.databinding.FragmentChooseChatBinding
import com.trialbot.trainyapplication.domain.contract.HasCustomAppbarIcon
import com.trialbot.trainyapplication.domain.contract.HasCustomTitle
import com.trialbot.trainyapplication.presentation.screen.chat.recycler.ChatAdapter
import com.trialbot.trainyapplication.presentation.screen.chat.recycler.ChatAdapterClickAction
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChooseChatFragment : Fragment(R.layout.fragment_choose_chat), ChatAdapterClickAction,
    HasCustomTitle, HasCustomAppbarIcon {

    private lateinit var binding: FragmentChooseChatBinding
    private val viewModel by viewModel<ChooseChatViewModel>()
    private val args: ChooseChatFragmentArgs by navArgs()
    private lateinit var adapter: ChatAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChooseChatBinding.bind(view)
        adapter = ChatAdapter(resources, this, args.currentUsername)

        with(binding) {
            chatsRV.adapter = adapter
            val layoutManager = LinearLayoutManager(requireContext())
            chatsRV.layoutManager = layoutManager
            chatsRV.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        viewModel.state.observe(viewLifecycleOwner, { state ->
            when(state) {
                is ChooseChatState.Loading -> {
                    with(binding) {
                        loadingPanel.visibility = View.VISIBLE
                        chatsRV.visibility = View.GONE
                    }
                }
                is ChooseChatState.Error -> {
                    with(binding) {
                        loadingPanel.visibility = View.GONE
                        Snackbar.make(binding.root, state.errorMessage, Snackbar.LENGTH_LONG).show()
                    }
                }
                is ChooseChatState.Success -> {
                    with(binding) {
                        loadingPanel.visibility = View.GONE
                        chatsRV.visibility = View.VISIBLE
                        adapter.updateChats(state.chats)
                    }
                }
            }
        })

        viewModel.render(args.currentUserId)

        viewModel.isChatAdded.observe(viewLifecycleOwner, { result ->
            if (result == true) {
                Toast.makeText(requireContext(), "User was successfully added", Toast.LENGTH_SHORT).show()
                afterAddingUser(true)
            } else if (result == false) {
                AlertDialog.Builder(requireContext()).apply {
                    setTitle("The adding was failed")
                    setMessage("User has not been added to chat. Maybe you don't have rights to add users")

                    setNeutralButton("Ok") { _, _ ->
                        afterAddingUser(false)
                    }
                    setOnCancelListener {
                        afterAddingUser(false)
                    }

                    setCancelable(true)
                }.create().show()
            }
        })
    }

    private fun afterAddingUser(isSuccess: Boolean) {
        if (isSuccess) {
            with(binding) {
                donePanel.visibility = View.VISIBLE
                if (doneAnimatedVector.drawable is AnimatedVectorDrawable)
                    (doneAnimatedVector.drawable as AnimatedVectorDrawable).start()
                else if (doneAnimatedVector.drawable is AnimatedVectorDrawableCompat)
                    (doneAnimatedVector.drawable as AnimatedVectorDrawableCompat).start()
            }
        }
        findNavController().navigateUp()
    }

    // Добавить юзера в выбранный чат
    override fun clickChat(chatId: Long, chatName: String, chatIconId: Int) {
        viewModel.addUserToChat(chatId, args.currentUserId, args.addedUserId)
    }

    override fun getTitle(): String {
        return "Add this user to"
    }

    override fun getIconRes(): Int? {
        return null
    }
}