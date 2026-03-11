package com.lhacenmed.budget.feature.detail.presentation

import androidx.compose.runtime.Composable
import com.lhacenmed.budget.core.navigation.INavigationItem
import com.lhacenmed.budget.core.navigation.INavigationManager
import com.lhacenmed.budget.core.navigation.IScreenProvider
import com.lhacenmed.budget.feature.detail.navigation.DetailRoute
import javax.inject.Inject

/**
 * Screen provider for Detail feature.
 * Provides screens for DetailRoute.
 */
class DetailScreenProvider @Inject constructor() : IScreenProvider {
    @Composable
    override fun provideScreen(
        route: INavigationItem,
        navigationManager: INavigationManager
    ): Boolean {
        return when (route) {
            is DetailRoute -> {
                DetailScreen(navigationManager, route.id)
                true
            }
            else -> false
        }
    }
}
