package com.trialbot.trainyapplication.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
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
import com.trialbot.trainyapplication.data.model.UserFull
import com.trialbot.trainyapplication.databinding.FragmentProfileBinding
import com.trialbot.trainyapplication.domain.UserAvatarUseCases
import com.trialbot.trainyapplication.domain.contract.HasCustomAppbarIcon
import com.trialbot.trainyapplication.domain.contract.HasCustomTitle
import com.trialbot.trainyapplication.presentation.recycler.avatar.AvatarAdapter
import com.trialbot.trainyapplication.presentation.recycler.avatar.AvatarAdapterClickAction
import com.trialbot.trainyapplication.presentation.state.ProfileState
import com.trialbot.trainyapplication.presentation.viewmodel.ProfileViewModel



class ProfileFragment : Fragment(R.layout.fragment_profile), AvatarAdapterClickAction,
    HasCustomTitle, HasCustomAppbarIcon {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var adapter: AvatarAdapter

    private val args: ProfileFragmentArgs by navArgs()

    private val viewModel: ProfileViewModel by viewModels {
        val prefs = requireActivity().getSharedPreferences(MyApp.SHARED_PREFS_AUTH_TAG, Context.MODE_PRIVATE) ?:
        throw Exception("Shared Preferences is null")

        ProfileViewModel.ProfileViewModelFactory(
            chatApi = (requireActivity().application as MyApp).api,
            sharedPrefs = prefs,
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentProfileBinding.bind(view)

        adapter = AvatarAdapter(requireContext().resources, this)

        val userId: Long = args.userId
        val username: String = args.username
        val userIcon: Int = args.userIcon

        val viewStatus: String = args.viewStatus
        viewModel.render(viewStatus, userId, username, userIcon)

        viewModel.state.observe(viewLifecycleOwner, {
            when(it) {
                is ProfileState.Loading -> {
                    with(binding) {
                        nameTV.visibility = View.GONE
                        avatarIV.visibility = View.GONE
                        statusTV.visibility = View.GONE
                        addToChatBtn.visibility = View.GONE
                        createTheChatBtn.visibility = View.GONE
                        editAboutBtn.visibility = View.GONE
                        changePasswordLL.visibility = View.GONE
                        logoutBtn.visibility = View.GONE
                        aboutTI.visibility = View.GONE
                        sendMessageBtn.visibility = View.GONE
                        errorLayout.visibility = View.GONE
                        avatarsRV.visibility = View.GONE
                        avatarCoverTransparent.visibility = View.GONE

                        loadingPanel.visibility = View.VISIBLE
                    }
                }
                is ProfileState.ReadOnly -> {
                    with(binding) {
                        nameTV.visibility = View.VISIBLE
                        avatarIV.visibility = View.VISIBLE
                        statusTV.visibility = View.VISIBLE
                        aboutTI.visibility = View.VISIBLE
                        addToChatBtn.visibility = View.VISIBLE
                        createTheChatBtn.visibility = View.GONE
                        editAboutBtn.visibility = View.GONE
                        changePasswordLL.visibility = View.GONE
                        logoutBtn.visibility = View.GONE
                        aboutTI.isEnabled = false
                        sendMessageBtn.visibility = View.VISIBLE
                        errorLayout.visibility = View.GONE
                        loadingPanel.visibility = View.GONE
                        avatarsRV.visibility = View.GONE
                        avatarCoverTransparent.visibility = View.GONE

                        if (it.user.isOnline)
                            statusTV.text = getString(R.string.user_online_status_online)
                        else
                            statusTV.text = getString(R.string.user_online_status_offline)

                        nameTV.text = it.user.username
                        avatarIV.setImageDrawable(UserAvatarUseCases.getDrawableFromId(it.user.icon, resources))

                        sendMessageBtn.setOnClickListener {
                            viewModel.sendMessageToUser()
                        }
                        addToChatBtn.setOnClickListener {
                            viewModel.addUserToChat()
                        }
                    }
                }
                is ProfileState.ReadWrite -> {
                    with(binding) {
                        nameTV.visibility = View.VISIBLE
                        avatarIV.visibility = View.VISIBLE
                        statusTV.visibility = View.VISIBLE
                        aboutTI.visibility = View.VISIBLE
                        addToChatBtn.visibility = View.GONE
                        createTheChatBtn.visibility = View.VISIBLE
                        editAboutBtn.visibility = View.VISIBLE
                        changePasswordLL.visibility = View.VISIBLE
                        logoutBtn.visibility = View.VISIBLE
                        aboutTI.isEnabled = true
                        sendMessageBtn.visibility = View.GONE
                        errorLayout.visibility = View.GONE
                        loadingPanel.visibility = View.GONE
                        avatarsRV.visibility = View.GONE
                        avatarCoverTransparent.visibility = View.GONE

                        statusTV.text = getString(R.string.user_online_status_online)
                        nameTV.text = it.user.username
                        avatarIV.setImageDrawable(UserAvatarUseCases.getDrawableFromId(it.user.icon, resources))

                        avatarIV.setOnClickListener {
                            viewModel.editAvatar()
                        }
                        editPasswordBtn.setOnClickListener {
                            viewModel.editPassword()
                        }
                        createTheChatBtn.setOnClickListener {
                            viewModel.createChat()
                        }
                    }
                }
                is ProfileState.AvatarChangingOpened -> {
                    with(binding) {
                        nameTV.visibility = View.VISIBLE
                        avatarIV.visibility = View.VISIBLE
                        statusTV.visibility = View.VISIBLE
                        aboutTI.visibility = View.VISIBLE
                        addToChatBtn.visibility = View.GONE
                        createTheChatBtn.visibility = View.VISIBLE
                        editAboutBtn.visibility = View.VISIBLE
                        changePasswordLL.visibility = View.VISIBLE
                        logoutBtn.visibility = View.VISIBLE
                        aboutTI.isEnabled = true
                        sendMessageBtn.visibility = View.GONE
                        errorLayout.visibility = View.GONE
                        loadingPanel.visibility = View.GONE

                        avatarCoverTransparent.visibility = View.VISIBLE
                        avatarsRV.visibility = View.VISIBLE

                        val layoutManager = LinearLayoutManager(context)
                        with(avatarsRV) {
                            this.layoutManager = layoutManager
                            this.adapter = this@ProfileFragment.adapter
                            addItemDecoration(DividerItemDecoration(
                                context,
                                DividerItemDecoration.VERTICAL
                            ))
                        }

                        avatarCoverTransparent.setOnClickListener {
                            if (viewModel.user != null && viewModel.user is UserFull) {
                                changeAvatar((viewModel.user as UserFull).icon)
                            }
                            else {
                                viewModel.cancelChangeAvatar()
                            }
                        }

                        adapter.setAvatarList(it.avatarList)
                    }
                }
                is ProfileState.AvatarChangingClosing -> {
                    with(binding) {
                        nameTV.visibility = View.VISIBLE
                        avatarIV.visibility = View.VISIBLE
                        statusTV.visibility = View.VISIBLE
                        aboutTI.visibility = View.VISIBLE
                        addToChatBtn.visibility = View.GONE
                        createTheChatBtn.visibility = View.VISIBLE
                        editAboutBtn.visibility = View.VISIBLE
                        changePasswordLL.visibility = View.VISIBLE
                        logoutBtn.visibility = View.VISIBLE
                        aboutTI.isEnabled = true
                        sendMessageBtn.visibility = View.GONE
                        errorLayout.visibility = View.GONE
                        loadingPanel.visibility = View.GONE

                        avatarsRV.visibility = View.GONE
                        avatarCoverTransparent.visibility = View.GONE

                        avatarIV.setImageDrawable(UserAvatarUseCases.getDrawableFromId(
                            id = it.newAvatarId,
                            res = requireContext().resources
                        ))
                    }
                }
                is ProfileState.Error -> {
                    with(binding) {
                        nameTV.visibility = View.GONE
                        avatarIV.visibility = View.GONE
                        statusTV.visibility = View.GONE
                        addToChatBtn.visibility = View.GONE
                        createTheChatBtn.visibility = View.GONE
                        editAboutBtn.visibility = View.GONE
                        changePasswordLL.visibility = View.GONE
                        logoutBtn.visibility = View.GONE
                        aboutTI.visibility = View.GONE
                        sendMessageBtn.visibility = View.GONE
                        loadingPanel.visibility = View.GONE
                        avatarsRV.visibility = View.GONE
                        avatarCoverTransparent.visibility = View.GONE

                        errorLayout.visibility = View.VISIBLE

                        Snackbar.make(binding.profileLayout, it.errorText, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        })

        with(binding) {
            logoutBtn.setOnClickListener {
                viewModel.logout()
                findNavController().navigate(
                    ProfileFragmentDirections.actionProfileFragmentToLoginFragment(),
                    navOptions {
                        anim {
                            enter = R.anim.enter
                            exit = R.anim.exit
                            popEnter = R.anim.pop_enter
                            popExit = R.anim.pop_exit
                        }
                    })
            }
        }
    }

    override fun getTitle(): String {
        return getString(R.string.user_profile_title)
    }

    override fun getIconRes(): Int? {
        return null
    }

    override fun changeAvatar(avatarId: Int) {
        viewModel.saveAvatar(avatarId)
    }
}