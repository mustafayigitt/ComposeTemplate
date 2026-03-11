package com.lhacenmed.budget.feature.splash.presentation

import com.lhacenmed.budget.core.base.BaseUiState
import com.lhacenmed.budget.core.navigation.INavigationItem

internal class SplashUiState(
    val destinationRoute: INavigationItem? = null,
    override val isLoading: Boolean = true,
    override val error: String? = null
) : BaseUiState(isLoading, error)