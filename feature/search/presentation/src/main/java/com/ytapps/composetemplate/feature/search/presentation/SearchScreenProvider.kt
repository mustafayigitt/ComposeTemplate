package com.lhacenmed.budget.feature.search.presentation

import androidx.compose.runtime.Composable
import com.lhacenmed.budget.core.navigation.INavigationItem
import com.lhacenmed.budget.core.navigation.INavigationManager
import com.lhacenmed.budget.core.navigation.IScreenProvider
import com.lhacenmed.budget.feature.search.navigation.SearchRoute
import javax.inject.Inject

/**
 * Screen provider for Search feature.
 * Provides screens for SearchRoute.
 */
class SearchScreenProvider @Inject constructor() : IScreenProvider {
    @Composable
    override fun provideScreen(
        route: INavigationItem,
        navigationManager: INavigationManager
    ): Boolean {
        return when (route) {
            is SearchRoute -> {
                SearchScreen(navigationManager)
                true
            }
            else -> false
        }
    }
}
