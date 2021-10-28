package com.trialbot.trainyapplication.presentation

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.databinding.FragmentLoginBinding
import com.trialbot.trainyapplication.presentation.state.LoginState
import com.trialbot.trainyapplication.presentation.viewmodel.LoginViewModel


class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding

    private val viewModel: LoginViewModel by viewModels {
        val prefs = requireActivity().getSharedPreferences(MyApp.SHARED_PREFS_AUTH_TAG, Context.MODE_PRIVATE) ?:
            throw Exception("Shared Preferences is null")

        LoginViewModel.LoginViewModelFactory(
            chatApi = (requireActivity().application as MyApp).api,
            sharedPrefs = prefs
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        requireActivity().actionBar?.setLogo(R.drawable.ic_logo)
        requireActivity().actionBar?.setDisplayShowHomeEnabled(true)
        requireActivity().actionBar?.setDisplayUseLogoEnabled(true)

        viewModel.render((requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager))

        viewModel.state.observe(viewLifecycleOwner, {
            when(it) {
                is LoginState.Loading -> {
                    with(binding) {
                        progressLoading.visibility = View.VISIBLE
                        loginBtn.isEnabled = false
                        registerBtn.isEnabled = false
                    }
                }
                is LoginState.Success -> {
                    with(binding) {
                        progressLoading.visibility = View.GONE
                        loginBtn.isEnabled = true
                        registerBtn.isEnabled = true
                    }
                    startMainActivity()
                }
                is LoginState.Error -> {
                    with(binding) {
                        progressLoading.visibility = View.GONE
                        loginBtn.isEnabled = false
                        registerBtn.isEnabled = false
                    }
                    Snackbar.make(binding.mainLayout, it.errorText, Snackbar.LENGTH_INDEFINITE).show()
                }
                is LoginState.UserNotFound -> {
                    with(binding) {
                        progressLoading.visibility = View.GONE
                        loginBtn.isEnabled = true
                        registerBtn.isEnabled = true
                    }
                    Log.e(MyApp.ERROR_LOG_TAG, "LoginActivity -> user not found in DB")
                    Snackbar.make(binding.mainLayout, it.message, Snackbar.LENGTH_INDEFINITE).show()
                }
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
        val intent = Intent(this, MainFragment::class.java)

        if (viewModel.username != null && viewModel.username!!.isNotBlank()) {
            intent.putExtra("username", viewModel.username)
            intent.putExtra("avatarId", viewModel.avatarId)
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

    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}