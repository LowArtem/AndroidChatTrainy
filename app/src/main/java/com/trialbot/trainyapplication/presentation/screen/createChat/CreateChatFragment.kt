package com.trialbot.trainyapplication.presentation.screen.createChat

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.databinding.FragmentCreateChatBinding
import com.trialbot.trainyapplication.domain.contract.HasCustomAppbarIcon
import com.trialbot.trainyapplication.domain.contract.HasCustomTitle
import com.trialbot.trainyapplication.presentation.screen.baseActivity.NavDrawerController
import com.trialbot.trainyapplication.presentation.screen.createChat.recycler.UserSearchAdapter
import com.trialbot.trainyapplication.utils.hideKeyboard
import com.trialbot.trainyapplication.utils.resultDialog
import org.koin.androidx.viewmodel.ext.android.viewModel


class CreateChatFragment : Fragment(R.layout.fragment_create_chat), HasCustomAppbarIcon, HasCustomTitle {

    private lateinit var binding: FragmentCreateChatBinding
    private lateinit var adapter: UserSearchAdapter

    private val args: CreateChatFragmentArgs by navArgs()
    private val viewModel by viewModel<CreateChatViewModel>()

    private var chatIcon: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateChatBinding.bind(view)

        (activity as NavDrawerController).setDrawerEnabled(false)

        viewModel.init(args.currentUserId)

        with(binding) {

            adapter = UserSearchAdapter(resources, viewModel)
            foundedUsersRV.adapter = adapter
            val layoutManager = LinearLayoutManager(requireContext())
            foundedUsersRV.layoutManager = layoutManager
            foundedUsersRV.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))


            viewModel.isChatSuccessfullyCreated.observe(viewLifecycleOwner, { result ->
                if (result != null) {
                    if (result) {
                        with(binding) {
                            chatCreatingNameET.text.clear()
                            chatCreatingAboutET.text.clear()
                        }
                    }
                    requireContext().resultDialog(
                        result = result,
                        textSuccess = getString(R.string.chat_has_created_alert_text),
                        textFailed = getString(R.string.chat_has_not_created_alert_text),
                        successfulAction = {
                            findNavController().navigateUp()
                        }
                    )
                }
            })

            viewModel.foundedUsers.observe(viewLifecycleOwner, { users ->
                adapter.updateSearchedUsers(users)
            })

            createChatBtn.setOnClickListener {
                if (chatCreatingNameET.text.isNotBlank() && chatCreatingNameET.text.length <= 25) {
                    hideKeyboard(requireActivity())
                    viewModel.createChat(
                        name = chatCreatingNameET.text.toString(),
                        icon = chatIcon,
                        currentUserId = args.currentUserId,
                        about = if (chatCreatingAboutET.text.isNotBlank()) chatCreatingNameET.text.toString() else null
                    )
                }
            }

            userSearchBtn.setOnClickListener {
                if (userSearchET.text.isNotBlank()) {
                    viewModel.searchUsers(userSearchET.text.toString())
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