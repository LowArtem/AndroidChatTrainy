package com.trialbot.trainyapplication.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.databinding.ActivityProfileBinding
import com.trialbot.trainyapplication.presentation.viewmodel.ProfileViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private val viewModel: ProfileViewModel by viewModels {
        val prefs = getSharedPreferences(MyApp.SHARED_PREFS_AUTH_TAG, Context.MODE_PRIVATE) ?:
        throw Exception("Shared Preferences is null")

        ProfileViewModel.ProfileViewModelFactory(
            chatApi = (application as MyApp).api,
            sharedPrefs = prefs
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onStart() {
        super.onStart()
        viewModel.render()
    }
}