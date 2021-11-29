package com.trialbot.trainyapplication.utils


import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavGraph
import androidx.navigation.navOptions
import com.trialbot.trainyapplication.R

fun NavController.navigateSafe(direction: NavDirections) {
    val destinationId = currentDestination?.getAction(direction.actionId)?.destinationId ?: -1
    currentDestination?.let { node ->
        val currentNode = when (node) {
            is NavGraph -> node
            else -> node.parent
        }
        if (destinationId != -1) {
            currentNode?.findNode(destinationId)?.let {
                navigate(direction, navOptions {
                    anim {
                        enter = R.anim.enter
                        exit = R.anim.exit
                        popEnter = R.anim.pop_enter
                        popExit = R.anim.pop_exit
                    }
                })
            }
        }
    }
}
