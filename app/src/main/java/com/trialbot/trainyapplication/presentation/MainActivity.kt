package com.trialbot.trainyapplication.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.databinding.ActivityMainBinding
import com.trialbot.trainyapplication.presentation.recycler.message.MessageAdapter
import com.trialbot.trainyapplication.presentation.viewmodel.MainViewModel

class MainActivity : AppCompatActivity(), TextView.OnEditorActionListener {

    private lateinit var binding: ActivityMainBinding
    private val adapter: MessageAdapter = MessageAdapter()

    private val viewModel: MainViewModel by viewModels {
        val prefs = getSharedPreferences(MyApp.SHARED_PREFS_AUTH_TAG, Context.MODE_PRIVATE) ?:
        throw Exception("Shared Preferences is null")

        MainViewModel.MainViewModelFactory(
            chatApi = (application as MyApp).api,
            sharedPrefs = prefs,
            adapter = adapter
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        with (binding)
        {
            setContentView(root)

            val layoutManager = LinearLayoutManager(this@MainActivity)
            messagesRV.layoutManager = layoutManager
            messagesRV.adapter = adapter

            sendBtn.setOnClickListener {
                sendMessage()
            }

            messageTextTV.setOnEditorActionListener(this@MainActivity)
        }

        // TODO: вместо ic_logo потом сделать аватарку пользователя
        supportActionBar?.setLogo(R.drawable.ic_default_avatar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayUseLogoEnabled(true)

        val username: String = intent.getStringExtra("username") ?: "Chat"
        supportActionBar?.title = username
    }

    override fun onStart() {
        super.onStart()
        viewModel.startMessageObserving()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val myMenuInflater = menuInflater
        myMenuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_exit -> {
                AlertDialog.Builder(this).apply {
                    setTitle("Подтверждение")
                    setMessage("Вы уверены, что хотите выйти из программы?")

                    setPositiveButton("Да") { _, _ ->
                        super.onBackPressed()
                    }

                    setNegativeButton("Нет"){_, _ -> }
                    setCancelable(true)
                }.create().show()
                true
            }
            R.id.action_logout -> {
                viewModel.logOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
}