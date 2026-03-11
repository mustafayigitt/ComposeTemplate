package com.lhacenmed.budget.feature.home.presentation

import androidx.compose.runtime.Composable
import com.lhacenmed.budget.core.navigation.INavigationItem
import com.lhacenmed.budget.core.navigation.INavigationManager
import com.lhacenmed.budget.core.navigation.IScreenProvider
import com.lhacenmed.budget.feature.home.navigation.HomeRoute
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
