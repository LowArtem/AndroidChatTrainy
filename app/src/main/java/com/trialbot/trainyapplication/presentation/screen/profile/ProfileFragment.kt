package com.trialbot.trainyapplication.presentation.screen.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.databinding.FragmentProfileBinding
import com.trialbot.trainyapplication.domain.contract.HasCustomAppbarIcon
import com.trialbot.trainyapplication.domain.contract.HasCustomTitle
import com.trialbot.trainyapplication.domain.model.UserFull
import com.trialbot.trainyapplication.presentation.drawable.DrawableController
import com.trialbot.trainyapplication.presentation.screen.profile.recycler.AvatarAdapter
import com.trialbot.trainyapplication.presentation.screen.profile.recycler.AvatarAdapterClickAction
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileFragment : Fragment(R.layout.fragment_profile), AvatarAdapterClickAction,
    HasCustomTitle, HasCustomAppbarIcon {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var adapter: AvatarAdapter

    private val args: ProfileFragmentArgs by navArgs()

    private val viewModel by viewModel<ProfileViewModel>()


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
                        newPasswordLL.visibility = View.GONE

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
                        newPasswordLL.visibility = View.GONE

                        if (it.user.isOnline)
                            statusTV.text = getString(R.string.user_online_status_online)
                        else
                            statusTV.text = getString(R.string.user_online_status_offline)

                        nameTV.text = it.user.username
                        avatarIV.setImageDrawable(DrawableController.getDrawableFromId(it.user.icon, resources))

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
                        avatarIV.setImageDrawable(DrawableController.getDrawableFromId(it.user.icon, resources))

                        avatarIV.setOnClickListener {
                            viewModel.editAvatar()
                        }

                        editPasswordBtn.setOnClickListener {
                            if (checkInputOldPassword()) {
                                changePasswordTI.error = null
                                newPasswordLL.visibility = View.VISIBLE
                            } else {
                                newPasswordLL.visibility = View.GONE
                                changePasswordTI.error = getString(R.string.wrong_password)
                            }
                        }

                        newPasswordConfirmBtn.setOnClickListener {
                            val newPassword: String = newPasswordSecondTI.text.toString()
                            if (checkInputNewPassword()) {
                                newPasswordFirstTI.error = null
                                viewModel.confirmNewPassword(newPassword) { result ->
                                    passwordChanged(result)
                                }
                            } else {
                                newPasswordFirstTI.error = getString(R.string.invalid_password)
                                newPasswordConfirmBtn.isEnabled = false
                            }
                        }

                        createTheChatBtn.setOnClickListener {
                            viewModel.createChat()
                        }

                        // Set confirmation button isEnable if text2 == text1
                        newPasswordSecondTI.addTextChangedListener(object : TextWatcher {
                            override fun beforeTextChanged(
                                p0: CharSequence?,
                                p1: Int,
                                p2: Int,
                                p3: Int
                            ) {}

                            override fun onTextChanged(
                                s: CharSequence?,
                                start: Int,
                                before: Int,
                                count: Int
                            ) {
                                newPasswordConfirmBtn.isEnabled = s.toString() == newPasswordFirstTI.text.toString()
                            }

                            override fun afterTextChanged(p0: Editable?) {}
                        })

                        newPasswordFirstTI.addTextChangedListener(object : TextWatcher {
                            override fun beforeTextChanged(
                                p0: CharSequence?,
                                p1: Int,
                                p2: Int,
                                p3: Int
                            ) {}

                            override fun onTextChanged(
                                s: CharSequence?,
                                start: Int,
                                before: Int,
                                count: Int
                            ) {
                                newPasswordConfirmBtn.isEnabled = false
                            }

                            override fun afterTextChanged(p0: Editable?) {}
                        })
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
                        newPasswordLL.visibility = View.GONE

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

                        avatarIV.setImageDrawable(DrawableController.getDrawableFromId(
                            id = it.newAvatarId,
                            res = requireContext().resources
                        ))

                        setFragmentResult(
                            TAG_AVATAR_ID_BUNDLE,
                            bundleOf(TAG_AVATAR_ID_BUNDLE to it.newAvatarId)
                        )
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
                        newPasswordLL.visibility = View.GONE

                        errorLayout.visibility = View.VISIBLE

                        Snackbar.make(binding.profileLayout, it.errorText, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        })

        with(binding) {
            newPasswordLL.visibility = View.GONE

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

    // Реакция на изменение пароля
    private fun passwordChanged(result: Boolean) {
        if (result) {
            AlertDialog.Builder(requireContext()).apply {
                setTitle("Password changed")
                setMessage("Password has successfully changed")

                setNeutralButton("Ok") { _, _ -> }

                setCancelable(true)
            }.create().show()
        }
        else {
            AlertDialog.Builder(requireContext()).apply {
                setTitle("The change was failed")
                setMessage("The password has not been changed. Try again or contact technical support")

                setNeutralButton("Ok") { _, _ -> }

                setCancelable(true)
            }.create().show()
        }

        with(binding) {
            changePasswordTI.text?.clear()
            newPasswordFirstTI.text?.clear()
            newPasswordSecondTI.text?.clear()

            newPasswordLL.visibility = View.GONE
        }
    }


    // Проверка корректности ввода пользователем password
    private fun checkInputOldPassword(): Boolean = with(binding) {
        if (viewModel.checkCurrentPassword(changePasswordTI.text.toString())) {
            newPasswordLL.visibility = View.VISIBLE

            return true
        }


        return false
    }

    private fun checkInputNewPassword(): Boolean = with(binding) {
         return@with newPasswordFirstTI.text!!.isNotBlank() && newPasswordFirstTI.length() > 5
    }

    companion object {
        const val TAG_AVATAR_ID_BUNDLE = "avatar_id_bundle"
    }
}