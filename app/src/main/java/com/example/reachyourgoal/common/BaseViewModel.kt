package com.example.reachyourgoal.common

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel<UiState, UiEvent, UiEffect> : ViewModel() {

    abstract val uiState: StateFlow<UiState>
    abstract val uiEffect: SharedFlow<UiEffect>

    abstract fun onEvent(event: UiEvent)

}