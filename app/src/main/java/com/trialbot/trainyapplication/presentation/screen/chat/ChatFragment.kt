package com.trialbot.trainyapplication.presentation.screen.chat

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.databinding.FragmentChatBinding
import com.trialbot.trainyapplication.domain.contract.HasCustomAppbarIcon
import com.trialbot.trainyapplication.domain.contract.HasCustomTitle
import com.trialbot.trainyapplication.presentation.screen.chat.recycler.ChatAdapter
import com.trialbot.trainyapplication.presentation.screen.chat.recycler.ChatAdapterClickAction
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatFragment : Fragment(R.layout.fragment_chat), ChatAdapterClickAction, HasCustomTitle, HasCustomAppbarIcon {

    private lateinit var binding: FragmentChatBinding
    private lateinit var adapter: ChatAdapter

    private val args: ChatFragmentArgs by navArgs()

    private val viewModel by viewModel<ChatViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatBinding.bind(view)

        adapter = ChatAdapter(requireContext().resources, this, args.username)

        with(binding.chatsRV) {
            val layoutManager = LinearLayoutManager(requireContext())
            this.layoutManager = layoutManager
            this.adapter = this@ChatFragment.adapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        viewModel.state.observe(viewLifecycleOwner, { state ->
            when(state) {
                is ChatState.Loading -> {
                    with(binding) {
                        loadingPanel.visibility = View.VISIBLE
                        textEmpty.visibility = View.GONE
                    }
                }
                is ChatState.Empty -> {
                    Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show()
                    with(binding) {
                        loadingPanel.visibility = View.GONE
                        textEmpty.visibility = View.VISIBLE
                    }
                }
                is ChatState.Success -> {
                    with(binding) {
                        loadingPanel.visibility = View.GONE
                        textEmpty.visibility = View.GONE
                    }
                    adapter.updateChats(state.chats)

                    // TODO: может быть обсервить чаты (но как-то не очень хочется)
                }
                is ChatState.Error -> {
                    with(binding) {
                        loadingPanel.visibility = View.GONE
                        textEmpty.visibility = View.GONE
                    }
                    Snackbar.make(binding.root, "Error: ${state.message}", Snackbar.LENGTH_LONG).show()
                }
            }
        })

        viewModel.render(args.userId)
    }

    override fun openChat(chatId: Long) {
        TODO("Not yet implemented")
    }

    override fun getTitle(): String {
        return "  ${args.username}"
    }

    override fun getIconRes(): Int {
        return args.iconId
    }
}