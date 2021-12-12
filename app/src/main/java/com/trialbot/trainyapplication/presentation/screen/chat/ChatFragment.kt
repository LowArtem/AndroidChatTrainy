package com.trialbot.trainyapplication.presentation.screen.chat

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.databinding.FragmentChatBinding
import com.trialbot.trainyapplication.domain.contract.HasCustomAppbarIcon
import com.trialbot.trainyapplication.domain.contract.HasCustomTitle
import com.trialbot.trainyapplication.presentation.screen.baseActivity.BaseActivity
import com.trialbot.trainyapplication.presentation.screen.baseActivity.NavDrawerController
import com.trialbot.trainyapplication.presentation.screen.chat.recycler.ChatAdapter
import com.trialbot.trainyapplication.presentation.screen.chat.recycler.ChatAdapterClickAction
import com.trialbot.trainyapplication.presentation.screen.message.MessageFragment
import com.trialbot.trainyapplication.presentation.screen.profile.ProfileFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChatFragment : Fragment(R.layout.fragment_chat), ChatAdapterClickAction, HasCustomTitle, HasCustomAppbarIcon {

    private lateinit var binding: FragmentChatBinding
    private lateinit var adapter: ChatAdapter

    val args: ChatFragmentArgs by navArgs()

    private val viewModel by viewModel<ChatViewModel>()

    private var drawerTitle: String = ""
    private var drawerImageId: Int = -1
    private var isIconChanged = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatBinding.bind(view)

        adapter = ChatAdapter(requireContext().resources, this, args.username)

        if (!isIconChanged) {
            drawerTitle = args.username
            drawerImageId = args.iconId
        }

        (activity as NavDrawerController).setDrawerEnabled(true)

        setFragmentResultListener(MessageFragment.USER_AVATAR_ICON_TAG) { _, bundle ->
            drawerImageId = bundle.getInt(MessageFragment.USER_AVATAR_ICON_TAG)
            (requireActivity() as BaseActivity).updateDrawerIcon(drawerImageId)
            isIconChanged = true
        }
        setFragmentResultListener(ProfileFragment.TAG_AVATAR_ID_BUNDLE) { _, bundle ->
            drawerImageId = bundle.getInt(ProfileFragment.TAG_AVATAR_ID_BUNDLE)
            (requireActivity() as BaseActivity).updateDrawerIcon(drawerImageId)
            isIconChanged = true
        }

        (requireActivity() as BaseActivity).updateDrawerIcon(drawerImageId)
        (requireActivity() as BaseActivity).updateDrawerTitle(drawerTitle)

        with(binding.chatsRV) {
            val layoutManager = LinearLayoutManager(requireContext())
            this.layoutManager = layoutManager
            this.adapter = this@ChatFragment.adapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

            // TODO: Нужно проверить работоспособность (прячет float button при скроллинге)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0 || dy < 0 && binding.createChatFloating.isShown)
                        binding.createChatFloating.hide()
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE)
                        binding.createChatFloating.show()
                    super.onScrollStateChanged(recyclerView, newState)
                }
            })
        }

        viewModel.state.observe(viewLifecycleOwner, { state ->
            when(state) {
                is ChatState.Loading -> {
                    with(binding) {
                        loadingPanel.visibility = View.VISIBLE
                        textEmpty.visibility = View.GONE
                        createChatFloating.visibility = View.GONE
                    }
                }
                is ChatState.Empty -> {
                    Toast.makeText(context, "Empty", Toast.LENGTH_SHORT).show()
                    with(binding) {
                        loadingPanel.visibility = View.GONE
                        textEmpty.visibility = View.VISIBLE
                        createChatFloating.visibility = View.VISIBLE
                    }
                }
                is ChatState.Success -> {
                    with(binding) {
                        loadingPanel.visibility = View.GONE
                        textEmpty.visibility = View.GONE
                        createChatFloating.visibility = View.VISIBLE
                    }
                    adapter.updateChats(state.chats)

                    // TODO: может быть обсервить чаты (но как-то не очень хочется)
                }
                is ChatState.Error -> {
                    with(binding) {
                        loadingPanel.visibility = View.GONE
                        textEmpty.visibility = View.GONE
                        createChatFloating.visibility = View.GONE
                    }
                    Snackbar.make(binding.root, "Error: ${state.message}", Snackbar.LENGTH_LONG).show()
                }
            }
        })

        with(binding) {
            createChatFloating.setOnClickListener {
                val direction = ChatFragmentDirections.actionChatFragmentToCreateChatFragment(args.userId)
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

        viewModel.render(args.userId)
    }

    override fun clickChat(chatId: Long, chatName: String, chatIconId: Int) {
        val direction = ChatFragmentDirections.actionChatFragmentToMessageFragment(chatName, chatIconId, chatId)
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
        return "Chat"
    }

    override fun getIconRes(): Int? {
        return null
    }

    fun openProfile(userIcon: Int) {
        val direction = ChatFragmentDirections.actionChatFragmentToProfileFragment2(
            viewStatus = "owner",
            userId = args.userId,
            username = args.username,
            userIcon = userIcon
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
}