package com.ytapps.composetemplate.core.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base ViewModel to standardize State and Event handling.
 * [S] represents the UI State.
 * [E] represents one-shot UI Events (e.g., Snackbars, Navigation).
 */
abstract class BaseViewModel<S, E> : ViewModel() {

    protected abstract val _uiState: MutableStateFlow<S>
    val uiState: StateFlow<S> by lazy { _uiState.asStateFlow() }

    private val _eventChannel by lazy { Channel<E>() }
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


