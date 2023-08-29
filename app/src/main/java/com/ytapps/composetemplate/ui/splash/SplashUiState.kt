package com.ytapps.composetemplate.ui.splash

import com.ytapps.composetemplate.core.base.BaseUiState

data class SplashUiState(
    val destinationRoute: String? = null,
    override val isLoading: Boolean = true,
    override val error: String? = null
) : BaseUiState(isLoading, error)