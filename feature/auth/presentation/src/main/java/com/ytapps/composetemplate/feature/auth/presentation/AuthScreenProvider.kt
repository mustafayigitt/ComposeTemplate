package com.lhacenmed.budget.feature.auth.presentation

import androidx.compose.runtime.Composable
import com.lhacenmed.budget.core.navigation.INavigationItem
import com.lhacenmed.budget.core.navigation.INavigationManager
import com.lhacenmed.budget.core.navigation.IScreenProvider
import com.lhacenmed.budget.feature.auth.navigation.LoginRoute
import javax.inject.Inject

/**
 * Screen provider for Auth feature module.
 * Provides screens for all Auth routes (LoginRoute, etc.).
 */
class AuthScreenProvider @Inject constructor() : IScreenProvider {
    @Composable
    override fun provideScreen(
        route: INavigationItem,
        navigationManager: INavigationManager
    ): Boolean {
        return when (route) {
            is LoginRoute -> {
                LoginScreen(navigationManager)
                true
            }
            else -> false
        }
    }
}
