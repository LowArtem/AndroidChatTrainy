package com.trialbot.trainyapplication.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.data.model.UserMessage
import com.trialbot.trainyapplication.databinding.ActivityMainBinding
import com.trialbot.trainyapplication.presentation.recycler.message.MessageAdapter
import com.trialbot.trainyapplication.presentation.recycler.message.MessageAdapterClickNavigation
import com.trialbot.trainyapplication.presentation.recycler.message.ProfileViewStatus
import com.trialbot.trainyapplication.presentation.state.MessageState
import com.trialbot.trainyapplication.presentation.viewmodel.MainViewModel

class MainActivity : AppCompatActivity(), TextView.OnEditorActionListener, MessageAdapterClickNavigation {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MessageAdapter

    private val viewModel: MainViewModel by viewModels {
        val prefs = getSharedPreferences(MyApp.SHARED_PREFS_AUTH_TAG, Context.MODE_PRIVATE) ?:
        throw Exception("Shared Preferences is null")

        MainViewModel.MainViewModelFactory(
            chatApi = (application as MyApp).api,
            sharedPrefs = prefs
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        adapter = MessageAdapter(viewModel.getCurrentUserId(), applicationContext.resources, this)

        with (binding)
        {
            setContentView(root)

            val layoutManager = LinearLayoutManager(this@MainActivity)
            messagesRV.layoutManager = layoutManager
            messagesRV.adapter = adapter
            messagesRV.addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))

            messagesRV.viewTreeObserver.addOnGlobalLayoutListener {
                (adapter.itemCount - 1).takeIf { it > 0 }?.let(messagesRV::smoothScrollToPosition)
            }

            sendBtn.setOnClickListener {
                sendMessage()
            }

            messageTextTV.setOnEditorActionListener(this@MainActivity)
        }

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayUseLogoEnabled(true)

        val username: String = intent.getStringExtra("username") ?: "Chat"
        var avatarId: Int = intent.getIntExtra("avatarId", R.drawable.ic_avatar_default)
        if (avatarId == -1) avatarId = R.drawable.ic_avatar_default

        viewModel.setUserIconId(avatarId)

        supportActionBar?.title = "  $username"
        supportActionBar?.setLogo(avatarId)

        viewModel.state.observe(this, { newValue ->
            when(newValue) {
                is MessageState.Loading -> {
                    with(binding) {
                        loadingPanel.visibility = View.VISIBLE
                        textEmpty.visibility = View.GONE
                    }
                    Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
                }
                is MessageState.Empty -> {
                    Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show()
                    with(binding) {
                        loadingPanel.visibility = View.GONE
                        textEmpty.visibility = View.VISIBLE
                    }
                }
                is MessageState.Success -> {
                    with(binding) {
                        loadingPanel.visibility = View.GONE
                        textEmpty.visibility = View.GONE
                    }
                    adapter.updateMessages(newValue.messages)
                    viewModel.messages.observe(this, {
                        adapter.updateMessages(it)
                    })
                }
                is MessageState.Error -> {
                    with(binding) {
                        loadingPanel.visibility = View.GONE
                        textEmpty.visibility = View.GONE
                    }
                    Snackbar.make(binding.mainLayout, "Error: ${newValue.errorText}", Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        viewModel.render()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val myMenuInflater = menuInflater
        myMenuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_exit -> {
                actionExitHandler()
                true
            }
            R.id.action_logout -> {
                actionLogoutHandler()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun actionLogoutHandler() {
        viewModel.logOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun actionExitHandler() {
        AlertDialog.Builder(this).apply {
            setTitle("Confirmation")
            setMessage("Are you sure, you want to exit?")

            setPositiveButton("Yes") { _, _ ->
                super.onBackPressed()
            }

            setNegativeButton("Cancel") { _, _ -> }
            setCancelable(true)
        }.create().show()
    }

    override fun onEditorAction(p0: TextView?, actionId: Int, p2: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEND)
        {
            sendMessage()
        }
        return true
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopMessageObserving()
        viewModel.applicationClosing()
    }

    private fun sendMessage() {
        with (binding) {
            viewModel.send(messageTextTV.text.toString())
            messageTextTV.text.clear()
        }
    }

    override fun openProfile(user: UserMessage, viewStatus: ProfileViewStatus) {
        val intent = Intent(this, ProfileActivity::class.java)

        when (viewStatus) {
            is ProfileViewStatus.Guest -> {
                intent.putExtra("viewStatus", "guest")
            }
            is ProfileViewStatus.Owner -> {
                intent.putExtra("viewStatus", "owner")
            }
        }

        intent.putExtra("user_id", user.id)
        intent.putExtra("user_username", user.username)
        intent.putExtra("user_icon", user.icon)
        startActivity(intent)
    }
}