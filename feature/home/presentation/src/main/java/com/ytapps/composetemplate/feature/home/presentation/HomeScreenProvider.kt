package com.ytapps.composetemplate.feature.home.presentation

import androidx.compose.runtime.Composable
import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.navigation.IScreenProvider
import com.ytapps.composetemplate.feature.home.navigation.HomeRoute
import javax.inject.Inject

/**
 * Screen provider for Home feature.
 * Provides screens for HomeRoute.
 */
class HomeScreenProvider @Inject constructor() : IScreenProvider {
    @Composable
    override fun provideScreen(
        route: INavigationItem,
        navigationManager: INavigationManager
    ): Boolean {
        return when (route) {
            is HomeRoute -> {
                HomeScreen(navigationManager)
                true
            }
            else -> false
        }
    }
}
