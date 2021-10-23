package com.trialbot.trainyapplication.domain

import android.util.Log
import com.trialbot.trainyapplication.MyApp
import com.trialbot.trainyapplication.data.AuthenticationControllerLocal
import com.trialbot.trainyapplication.data.AuthenticationControllerRemote
import com.trialbot.trainyapplication.data.model.UserFull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class StartStopRemoteActions(
    private val authControllerRemote: AuthenticationControllerRemote,
    private val authControllerLocal: AuthenticationControllerLocal
) {
    suspend fun appClosed(userIconId: Int) = withContext(Dispatchers.IO) {
        Log.d(MyApp.DEBUG_LOG_TAG, "App closed")

        val user = authControllerLocal.getCredentials()
        if (user != null) {
            authControllerRemote.updateUser(
                id = user.id,
                user = UserFull(user.id, userIconId, user.username, user.password, false, Calendar.getInstance().time)
            )
        }
        else {
            Log.e(MyApp.ERROR_LOG_TAG, "StartStopRemoteActions.appClosed() -> cannot get local saved credentials")
        }
    }

    suspend fun appStarted(userIconId: Int) = withContext(Dispatchers.IO) {
        Log.d(MyApp.DEBUG_LOG_TAG, "App started")

        val user = authControllerLocal.getCredentials()
        if (user != null) {
            authControllerRemote.updateUser(
                id = user.id,
                user = UserFull(user.id, userIconId, user.username, user.password, true, Calendar.getInstance().time)
            )
        }
        else {
            Log.e(MyApp.ERROR_LOG_TAG, "StartStopRemoteActions.appStarted() -> cannot get local saved credentials")
        }
    }
}