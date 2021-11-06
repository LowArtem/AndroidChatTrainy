package com.trialbot.trainyapplication.presentation.screen.chat

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.trialbot.trainyapplication.databinding.FragmentChatBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding

    private val args: ChatFragmentArgs by navArgs()

    private val viewModel by viewModel<ChatViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatBinding.bind(view)

        viewModel.state.observe(viewLifecycleOwner, { state ->
            when(state) {
                is ChatState.Loading -> {

                }
                is ChatState.Empty -> {

                }
                is ChatState.Success -> {

                }
                is ChatState.Error -> {

                }
            }
        })

        viewModel.render()
    }
}