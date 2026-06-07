package com.ytapps.composetemplate.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base ViewModel to standardize State and Event handling.
 * [S] represents the UI State.
 * [E] represents one-shot UI Events (e.g., Snackbars, Navigation).
 */
abstract class BaseViewModel<S, E>(initialState: S) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)
    val uiState = _uiState.asStateFlow()

    private val _eventChannel = Channel<E>()
    val events = _eventChannel.receiveAsFlow()

    protected fun updateState(update: (S) -> S) {
        _uiState.update(update)
    }

    protected fun sendEvent(event: E) {
        viewModelScope.launch {
            _eventChannel.send(event)
        }
    }
}

/**
 * Common UI Events used across features.
 */
sealed interface CommonUiEvent {
    data class ShowSnackbar(val message: String) : CommonUiEvent
    data object Unauthorized : CommonUiEvent
}
