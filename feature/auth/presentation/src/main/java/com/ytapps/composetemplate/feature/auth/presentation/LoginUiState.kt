package com.ytapps.composetemplate.feature.auth.presentation

internal data class LoginUiState(
    val isLoading: Boolean = false,
    val email: String = "",
    val password: String = "",
)
