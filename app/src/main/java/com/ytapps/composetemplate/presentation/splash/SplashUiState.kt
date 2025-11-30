package com.ytapps.composetemplate.presentation.splash

import com.ytapps.composetemplate.core.base.BaseUiState
import com.ytapps.composetemplate.core.navigation.INavigationItem

data class SplashUiState(
    val destinationRoute: INavigationItem? = null,
    override val isLoading: Boolean = true,
    override val error: String? = null
) : BaseUiState(isLoading, error)