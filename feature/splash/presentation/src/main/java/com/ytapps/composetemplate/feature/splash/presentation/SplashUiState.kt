package com.ytapps.composetemplate.feature.splash.presentation

import com.ytapps.composetemplate.core.navigation.INavigationItem

internal data class SplashUiState(
    val destinationRoute: INavigationItem? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
)
