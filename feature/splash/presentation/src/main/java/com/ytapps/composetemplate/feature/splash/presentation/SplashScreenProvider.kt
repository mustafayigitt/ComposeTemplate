package com.ytapps.composetemplate.feature.splash.presentation

import androidx.compose.runtime.Composable
import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.navigation.IScreenProvider
import com.ytapps.composetemplate.feature.splash.navigation.SplashRoute
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
