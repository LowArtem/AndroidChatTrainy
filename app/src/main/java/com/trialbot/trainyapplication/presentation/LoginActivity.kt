package com.trialbot.trainyapplication.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.databinding.ActivityLoginBinding
import com.trialbot.trainyapplication.presentation.viewmodel.LoginViewModel


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val viewModel: LoginViewModel by viewModels {
        val prefs = getSharedPreferences(MyApp.SHARED_PREFS_AUTH_TAG, Context.MODE_PRIVATE) ?:
            throw Exception("Shared Preferences is null")

        LoginViewModel.LoginViewModelFactory(
            chatApi = (application as MyApp).api,
            sharedPrefs = prefs
        )
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setLogo(R.drawable.ic_logo)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayUseLogoEnabled(true)


        viewModel.getUserLoginStatus()

        if (viewModel.isLoginSuccessful.value == true) {
            startMainActivity()
        }

        viewModel.isLoginSuccessful.observe(this, {
            if (it) {
                startMainActivity()
            }
        })

        with (binding) {
            loginBtn.setOnClickListener {
                if (checkInput()) {
                    viewModel.login(
                        usernameEntered = usernameEdit.text.toString(),
                        passwordEntered = passwordEdit.text.toString(),
                    )
                }
            }

            registerBtn.setOnClickListener {
                if (checkInput()) {
                    viewModel.register(
                        usernameEntered = usernameEdit.text.toString(),
                        passwordEntered = passwordEdit.text.toString(),
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        viewModel.saveUserLoginStatus()
        super.onDestroy()
    }

    private fun startMainActivity() {
        viewModel.setUserOnline()
        val intent = Intent(this, MainActivity::class.java)

        if (viewModel.username != null && viewModel.username!!.isNotBlank()) {
            intent.putExtra("username", viewModel.username)
            intent.putExtra("avatarId", viewModel.avatarId)
            // TODO: добавить потом аватар, и может что-то еще
        }
        else {
            Log.e(MyApp.DEBUG_LOG_TAG, "LoginActivity.startMainActivity() -> viewModel.username is null or empty")
        }
        startActivity(intent)
        finish()
    }

    // Проверка корректности ввода пользователем username и password
    private fun checkInput(): Boolean = with (binding){
        if (usernameEdit.text == null || passwordEdit.text == null) return false
        if (
            usernameEdit.text!!.isNotBlank() &&
            passwordEdit.text!!.isNotBlank() &&
            passwordEdit.length() > 5
        ) {
            usernameEdit.error = null
            passwordEdit.error = null

            return true
        }
        else {
            if (usernameEdit.text!!.isBlank()) {
                usernameEdit.error = "Please enter the username"
            }
            else {
                passwordEdit.error = "Please enter the password more than 5 symbols"
            }

            return false
        }
    }

}