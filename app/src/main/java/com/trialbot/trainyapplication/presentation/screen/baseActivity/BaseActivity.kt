package com.trialbot.trainyapplication.presentation.screen.baseActivity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.trialbot.trainyapplication.R
import com.trialbot.trainyapplication.databinding.ActivityBaseBinding
import com.trialbot.trainyapplication.domain.contract.HasCustomAppbarIcon
import com.trialbot.trainyapplication.domain.contract.HasCustomTitle
import com.trialbot.trainyapplication.domain.contract.HasDisplayHomeDisabled
import com.trialbot.trainyapplication.domain.utils.logD
import com.trialbot.trainyapplication.presentation.screen.chat.ChatFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class BaseActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityBaseBinding

    private val viewModel by viewModel<BaseViewModel>()

    private val topLevelDestinations = setOf(R.id.loginFragment, R.id.chatFragment)

    private var currentFragment: Fragment? = null
    private var currentDestination: NavDestination? = null
    private var isRestart = false

    private var user_id: Long = -1
    private var user_username: String = ""
    private var user_icon: Int = -1

    private val fragmentListener = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            if (f is NavHostFragment) return
            currentFragment = f
            updateUi()
            if (f is ChatFragment) {
                getUserData()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentLayout) as NavHostFragment
        navController = navHost.navController

        val appBarConfiguration = AppBarConfiguration(topLevelDestinations, binding.root)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            logD("Changed destination to -> ${destination.label}")
            currentDestination = destination
        }
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener, true)


        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profileFragment -> {
                    (currentFragment as ChatFragment).openProfile(user_icon)
                    binding.root.closeDrawer(GravityCompat.START)
                    true
                }
                else -> {
                    true
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                if (topLevelDestinations.contains(currentDestination?.id)) {
                    if (binding.root.isDrawerOpen(GravityCompat.START)) {
                        binding.root.closeDrawer(GravityCompat.START)
                    } else {
                        binding.root.openDrawer(GravityCompat.START)
                    }
                } else {
                    navController.navigateUp()
                }

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        if (isRestart) {
            viewModel.applicationStarted()
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.applicationClosing()
        isRestart = true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (isTopLevelDestination(navController.currentDestination)) {
            finish()
        } else {
            navController.popBackStack()
        }
    }

    private fun isTopLevelDestination(destination: NavDestination?): Boolean {
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

        if (fragment is HasDisplayHomeDisabled) {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun getUserData() {
        val fragment = currentFragment as ChatFragment
        user_id = fragment.args.userId
        user_username = fragment.args.username
    }

    fun updateDrawerIcon(@DrawableRes iconId: Int) {
        val image = binding.navView.getHeaderView(0).findViewById(R.id.avatarIV) as ImageView

        if (iconId == -1) {
            image.setImageResource(R.drawable.ic_avatar_default)
            user_icon = R.drawable.ic_avatar_default
        } else {
            image.setImageResource(iconId)
            user_icon = iconId
        }
    }

    fun updateDrawerTitle(title: String) {
        val text = binding.navView.getHeaderView(0).findViewById(R.id.nameTV) as TextView
        text.text = title
    }
}