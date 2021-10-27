package com.trialbot.trainyapplication.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.databinding.ActivityProfileBinding
import com.trialbot.trainyapplication.domain.UserAvatarUseCases
import com.trialbot.trainyapplication.presentation.viewmodel.ProfileViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private lateinit var viewModel: ProfileViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId: Long = intent.getLongExtra("user_id", -1)
        val username: String = intent.getStringExtra("user_username") ?: "Username"
        val userIcon: Int = intent.getIntExtra("user_icon", -1)

        val prefs = getSharedPreferences(MyApp.SHARED_PREFS_AUTH_TAG, Context.MODE_PRIVATE) ?:
        throw Exception("Shared Preferences is null")
        viewModel = ProfileViewModel.ProfileViewModelFactory(
            chatApi = (application as MyApp).api,
            sharedPrefs = prefs,
            userId
        ).create(ProfileViewModel::class.java)



        val viewStatus: String = intent.getStringExtra("viewStatus") ?: "guest"
        when(viewStatus) {
            "guest" -> {
                with(binding) {
                    addToChatBtn.visibility = View.VISIBLE
                    createTheChatBtn.visibility = View.GONE
                    editAboutBtn.visibility = View.GONE
                    changePasswordLL.visibility = View.GONE
                    logoutBtn.visibility = View.GONE
                    aboutTI.isEnabled = false
                    sendMessageBtn.visibility = View.VISIBLE

                    viewModel.userIsOnline.observe(this@ProfileActivity, {
                        if (it == true) {
                            statusTV.text = getString(R.string.user_online_status_online)
                        }
                        else {
                            statusTV.text = getString(R.string.user_online_status_offline)
                        }
                    })
                }
            }
            "owner" -> {
                with(binding) {
                    statusTV.text = getString(R.string.user_online_status_online)
                    addToChatBtn.visibility = View.GONE
                    createTheChatBtn.visibility = View.VISIBLE
                    editAboutBtn.visibility = View.VISIBLE
                    changePasswordLL.visibility = View.VISIBLE
                    logoutBtn.visibility = View.VISIBLE
                    aboutTI.isEnabled = true
                    sendMessageBtn.visibility = View.GONE
                }
            }
            else -> {
                throw IllegalArgumentException("Unknown profile view status")
            }
        }

        with(binding) {
            nameTV.text = username
            avatarIV.setImageDrawable(UserAvatarUseCases.getDrawableFromId(userIcon, resources))
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.render()
    }
}