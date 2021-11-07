package com.trialbot.trainyapplication.presentation.screen.baseActivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trialbot.trainyapplication.domain.StartStopRemoteActions
import kotlinx.coroutines.launch

class BaseViewModel(private val startStopRemoteActions: StartStopRemoteActions) : ViewModel() {

    fun applicationStarted() {
        viewModelScope.launch {
            startStopRemoteActions.appStarted()
        }
    }

    fun applicationClosing() {
        viewModelScope.launch {
            startStopRemoteActions.appClosed()
        }
        Thread.sleep(1000)
    }
}