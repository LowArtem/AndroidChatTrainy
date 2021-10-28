package com.trialbot.trainyapplication.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.databinding.ActivityProfileBinding
import com.trialbot.trainyapplication.domain.UserAvatarUseCases
import com.trialbot.trainyapplication.presentation.state.ProfileState
import com.trialbot.trainyapplication.presentation.viewmodel.ProfileViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private val viewModel: ProfileViewModel by viewModels {
        val prefs = getSharedPreferences(MyApp.SHARED_PREFS_AUTH_TAG, Context.MODE_PRIVATE) ?:
        throw Exception("Shared Preferences is null")

        ProfileViewModel.ProfileViewModelFactory(
            chatApi = (application as MyApp).api,
            sharedPrefs = prefs,
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "User profile"

        val userId: Long = intent.getLongExtra("user_id", -1)
        val username: String = intent.getStringExtra("user_username") ?: "Username"
        val userIcon: Int = intent.getIntExtra("user_icon", -1)

        val viewStatus: String = intent.getStringExtra("viewStatus") ?: "guest"
        viewModel.render(viewStatus, userId, username, userIcon)

        viewModel.state.observe(this, {
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
            val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}