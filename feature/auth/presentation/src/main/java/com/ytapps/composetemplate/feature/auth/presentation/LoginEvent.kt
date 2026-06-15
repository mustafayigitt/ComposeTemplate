package com.ytapps.composetemplate.feature.auth.presentation

sealed interface LoginEvent {
    data object NavigateToSplash : LoginEvent
    data class ShowSnackbar(val message: String) : LoginEvent
}
