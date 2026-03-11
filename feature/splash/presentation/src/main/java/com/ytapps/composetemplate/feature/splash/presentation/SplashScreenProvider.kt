package com.lhacenmed.budget.feature.splash.presentation

import androidx.compose.runtime.Composable
import com.lhacenmed.budget.core.navigation.INavigationItem
import com.lhacenmed.budget.core.navigation.INavigationManager
import com.lhacenmed.budget.core.navigation.IScreenProvider
import com.lhacenmed.budget.feature.splash.navigation.SplashRoute
import javax.inject.Inject

/**
 * Screen provider for Splash feature.
 * Provides screens for SplashRoute.
 */
class SplashScreenProvider @Inject constructor() : IScreenProvider {
    @Composable
    override fun provideScreen(
        route: INavigationItem,
        navigationManager: INavigationManager
    ): Boolean {
        return when (route) {
            is SplashRoute -> {
                SplashScreen(navigationManager)
                true
            }
            else -> false
        }
    }
}
