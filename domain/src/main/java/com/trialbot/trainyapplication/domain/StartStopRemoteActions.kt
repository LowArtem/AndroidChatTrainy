package com.trialbot.trainyapplication.domain

import com.trialbot.trainyapplication.domain.interfaces.AuthenticationControllerLocal
import com.trialbot.trainyapplication.domain.interfaces.AuthenticationControllerRemote
import com.trialbot.trainyapplication.domain.model.UserFull

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class StartStopRemoteActions(
    private val authControllerRemote: AuthenticationControllerRemote,
    private val authControllerLocal: AuthenticationControllerLocal
) {

    suspend fun appClosed() = withContext(Dispatchers.IO) {
        val user = authControllerLocal.getCredentials()
        if (user != null) {
            authControllerRemote.updateUser(
                id = user.id,
                user = UserFull(user.id, user.icon, user.username, user.password, false, Calendar.getInstance().time)
            )
        }
        else {
//            Log.e(MyApp.ERROR_LOG_TAG, "StartStopRemoteActions.appClosed() -> cannot get local saved credentials")
        }
    }

    suspend fun appStarted() = withContext(Dispatchers.IO) {
        val user = authControllerLocal.getCredentials()
        if (user != null) {
            authControllerRemote.updateUser(
                id = user.id,
                user = UserFull(user.id, user.icon, user.username, user.password, true, Calendar.getInstance().time)
            )
        }
        else {
//            Log.e(MyApp.ERROR_LOG_TAG, "StartStopRemoteActions.appStarted() -> cannot get local saved credentials")
        }
    }
}