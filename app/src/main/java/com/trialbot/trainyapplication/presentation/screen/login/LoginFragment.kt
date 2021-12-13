package com.trialbot.trainyapplication.presentation.screen.login

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.databinding.FragmentLoginBinding
import com.trialbot.trainyapplication.domain.contract.HasDisplayHomeDisabled
import com.trialbot.trainyapplication.domain.utils.logE
import com.trialbot.trainyapplication.presentation.screen.baseActivity.NavDrawerController
import com.trialbot.trainyapplication.utils.hideKeyboard
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginFragment : Fragment(R.layout.fragment_login), HasDisplayHomeDisabled {

    private lateinit var binding: FragmentLoginBinding

    private val viewModel by viewModel<LoginViewModel>()

    private lateinit var firebaseAnalytics: FirebaseAnalytics


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())

        binding = FragmentLoginBinding.bind(view)

        viewModel.render(
            connectivityManager = (requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager),
            firebaseAnalytics = firebaseAnalytics
        )

        (activity as NavDrawerController).setDrawerEnabled(false)

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
                    Snackbar.make(binding.loginLayout, it.errorText, Snackbar.LENGTH_LONG).show()
                }
                is LoginState.UserNotFound -> {
                    with(binding) {
                        progressLoading.visibility = View.GONE
                        loginBtn.isEnabled = true
                        registerBtn.isEnabled = true
                    }
                    logE("LoginActivity -> user not found in DB")
                    Snackbar.make(binding.loginLayout, it.message, Snackbar.LENGTH_LONG).show()
                }
            }
        })

        with (binding) {
            loginBtn.setOnClickListener {
                if (checkInput()) {
                    hideKeyboard(requireActivity())

                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.VALUE, "user")
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)

                    viewModel.login(
                        usernameEntered = usernameEdit.text.toString(),
                        passwordEntered = passwordEdit.text.toString(),
                    )
                }
            }

            registerBtn.setOnClickListener {
                if (checkInput()) {
                    hideKeyboard(requireActivity())

                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.VALUE, "email")
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)

                    viewModel.register(
                        usernameEntered = usernameEdit.text.toString(),
                        passwordEntered = passwordEdit.text.toString(),
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        viewModel.saveUserLoginStatus()
        super.onDestroyView()
    }

    private fun startMainActivity() {
        if (viewModel.username != null && viewModel.username!!.isNotBlank() && viewModel.userId != -1L) {
            viewModel.applicationStarted()
            val direction = LoginFragmentDirections.actionLoginFragmentToChatFragment(
                viewModel.username!!,
                viewModel.avatarId,
                viewModel.userId
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
        else {
            logE("viewModel.username or userId is null or empty")
            viewModel.setOutsideError("Username is empty")
        }
    }

    // Проверка корректности ввода пользователем username и password
    private fun checkInput(): Boolean = with (binding){
        if (usernameEdit.text == null || passwordEdit.text == null) return false
        if (
            usernameEdit.text!!.isNotBlank() &&
            passwordEdit.text!!.isNotBlank() &&
            passwordEdit.length() > 5 &&
            usernameEdit.text!!.length <= 26
        ) {
            usernameEdit.error = null
            passwordEdit.error = null

            return true
        }
        else {
            when {
                usernameEdit.text!!.isBlank() -> {
                    usernameEdit.error = "Please enter the username"
                }
                usernameEdit.text!!.length > 26 -> {
                    usernameEdit.error = "The username is too long"
                }
                else -> {
                    passwordEdit.error = "Please enter the password more than 5 symbols"
                }
            }

            return false
        }
    }
}