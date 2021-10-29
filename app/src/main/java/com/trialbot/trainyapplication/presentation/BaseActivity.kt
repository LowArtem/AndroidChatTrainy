package com.trialbot.trainyapplication.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.R

class BaseActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private val topLevelDestinations = setOf(R.id.loginFragment, R.id.mainFragment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentLayout) as NavHostFragment
        navController = navHost.navController

        val appBarConfiguration = AppBarConfiguration.Builder(R.id.loginFragment, R.id.mainFragment).build()
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d(MyApp.DEBUG_LOG_TAG, "Changed destination to -> ${destination.label}")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (isStartDestination(navController.currentDestination)) {
            finish()
        } else {
            navController.popBackStack()
        }
    }

    private fun isStartDestination(destination: NavDestination?): Boolean {
        if (destination == null) return false
        return topLevelDestinations.contains(destination.id)
    }
}