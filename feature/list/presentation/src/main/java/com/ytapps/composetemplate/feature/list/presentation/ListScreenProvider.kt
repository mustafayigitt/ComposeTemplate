package com.lhacenmed.budget.feature.list.presentation

import androidx.compose.runtime.Composable
import com.lhacenmed.budget.core.navigation.INavigationItem
import com.lhacenmed.budget.core.navigation.INavigationManager
import com.lhacenmed.budget.core.navigation.IScreenProvider
import com.lhacenmed.budget.feature.list.navigation.ListRoute
import javax.inject.Inject

/**
 * Screen provider for List feature.
 * Provides screens for ListRoute.
 */
class ListScreenProvider @Inject constructor() : IScreenProvider {
    @Composable
    override fun provideScreen(
        route: INavigationItem,
        navigationManager: INavigationManager
    ): Boolean {
        return when (route) {
            is ListRoute -> {
                ListScreen(navigationManager)
                true
            }
            else -> false
        }
    }
}
