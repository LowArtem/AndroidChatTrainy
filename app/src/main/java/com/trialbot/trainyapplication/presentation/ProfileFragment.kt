package com.trialbot.trainyapplication.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.databinding.FragmentProfileBinding
import com.trialbot.trainyapplication.domain.UserAvatarUseCases
import com.trialbot.trainyapplication.presentation.state.ProfileState
import com.trialbot.trainyapplication.presentation.viewmodel.ProfileViewModel

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding

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

        requireActivity().actionBar?.setDisplayHomeAsUpEnabled(true)
        requireActivity().actionBar?.title = "User profile"

        val userId: Long = intent.getLongExtra("user_id", -1)
        val username: String = intent.getStringExtra("user_username") ?: "Username"
        val userIcon: Int = intent.getIntExtra("user_icon", -1)

        val viewStatus: String = intent.getStringExtra("viewStatus") ?: "guest"
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

                        errorLayout.visibility = View.VISIBLE

                        Snackbar.make(binding.mainLayout, it.errorText, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        })

        binding.logoutBtn.setOnClickListener {
            viewModel.logout()
            val intent = Intent(this@ProfileFragment, LoginFragment::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}