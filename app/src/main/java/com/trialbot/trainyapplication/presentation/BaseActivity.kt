package com.trialbot.trainyapplication.presentation

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.trialbot.trainyapplication.R

class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val myMenuInflater = menuInflater
        myMenuInflater.inflate(R.menu.menu, menu)
        return true
    }
}