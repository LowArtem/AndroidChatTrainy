package com.trialbot.trainyapplication.presentation.screen.createChat

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.databinding.FragmentCreateChatBinding
import com.trialbot.trainyapplication.domain.contract.HasCustomAppbarIcon
import com.trialbot.trainyapplication.domain.contract.HasCustomTitle
import org.koin.androidx.viewmodel.ext.android.viewModel


class CreateChatFragment : Fragment(R.layout.fragment_create_chat), HasCustomAppbarIcon, HasCustomTitle {

    private lateinit var binding: FragmentCreateChatBinding
    private val args: CreateChatFragmentArgs by navArgs()
    private val viewModel by viewModel<CreateChatViewModel>()

    private var chatIcon: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateChatBinding.bind(view)

        viewModel.init(args.currentUserId)

        with(binding) {

            viewModel.isChatSuccessfullyCreated.observe(viewLifecycleOwner, { result ->
                if (result != null) {
                    if (result) {
                        AlertDialog.Builder(requireContext()).apply {
                            setTitle("Chat created")
                            setMessage(getString(R.string.chat_has_created_alert_text))

                            setNeutralButton("Ok") { _, _ ->
                                findNavController().navigateUp()
                            }

                            setCancelable(true)
                        }.create().show()

                        with(binding) {
                            chatCreatingNameET.text.clear()
                            chatCreatingAboutET.text.clear()
                        }

                    } else {
                        AlertDialog.Builder(requireContext()).apply {
                            setTitle("The creating was failed")
                            setMessage(getString(R.string.chat_has_not_created_alert_text))

                            setNeutralButton("Ok") { _, _ -> }

                            setCancelable(true)
                        }.create().show()
                    }
                }
            })

            createChatBtn.setOnClickListener {
                if (chatCreatingNameET.text.isNotBlank()) {
                    viewModel.createChat(
                        name = chatCreatingNameET.text.toString(),
                        icon = chatIcon,
                        about = if (chatCreatingAboutET.text.isNotBlank()) chatCreatingNameET.text.toString() else null
                    )
                }
            }
        }
    }

    override fun getIconRes(): Int? {
        return null
    }

    override fun getTitle(): String {
        return " Creating"
    }
}