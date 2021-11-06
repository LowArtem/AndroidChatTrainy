package com.trialbot.trainyapplication.presentation.screen.baseActivity

import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.domain.contract.HasCustomAppbarIcon
import com.trialbot.trainyapplication.domain.contract.HasCustomTitle
import com.trialbot.trainyapplication.domain.utils.logD

class BaseActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    private val topLevelDestinations = setOf(R.id.loginFragment, R.id.messageFragment)
    private var currentFragment: Fragment? = null

    private val fragmentListener = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            if (f is NavHostFragment) return
            currentFragment = f
            updateUi()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentLayout) as NavHostFragment
        navController = navHost.navController

        NavigationUI.setupActionBarWithNavController(this, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            logD("Changed destination to -> ${destination.label}")
        }
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener, true)
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

    private fun updateUi() {
        val fragment = currentFragment

        if (fragment is HasCustomTitle) {
            supportActionBar?.title = fragment.getTitle()
        } else {
            supportActionBar?.title = getString(R.string.app_name)
        }

        if (fragment is HasCustomAppbarIcon) {
            if (fragment.getIconRes() == null) {
                supportActionBar?.setDisplayUseLogoEnabled(false)
                supportActionBar?.setDisplayShowHomeEnabled(false)
            } else {
                supportActionBar?.setDisplayUseLogoEnabled(true)
                supportActionBar?.setDisplayShowHomeEnabled(true)

                if (fragment.getIconRes() == -1) {
                    supportActionBar?.setIcon(R.drawable.ic_avatar_default)
                }
                else {
                    supportActionBar?.setIcon(fragment.getIconRes()!!)
                }
            }
        } else {
            supportActionBar?.setDisplayUseLogoEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.setIcon(R.drawable.ic_logo)
        }

        if (isStartDestination(navController.currentDestination)) {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    fun updateActionBarIcon(@DrawableRes iconId: Int) {
        if (iconId == -1) {
            supportActionBar?.setIcon(R.drawable.ic_avatar_default)
        } else {
            supportActionBar?.setIcon(iconId)
        }
    }
}