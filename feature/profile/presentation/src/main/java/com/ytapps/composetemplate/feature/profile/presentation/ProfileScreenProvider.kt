package com.lhacenmed.budget.feature.profile.presentation

import androidx.compose.runtime.Composable
import com.lhacenmed.budget.core.navigation.INavigationItem
import com.lhacenmed.budget.core.navigation.INavigationManager
import com.lhacenmed.budget.core.navigation.IScreenProvider
import com.lhacenmed.budget.feature.profile.navigation.ProfileRoute
import javax.inject.Inject

/**
 * Screen provider for Profile feature.
 * Provides screens for ProfileRoute.
 */
class ProfileScreenProvider @Inject constructor() : IScreenProvider {
    @Composable
    override fun provideScreen(
        route: INavigationItem,
        navigationManager: INavigationManager
    ): Boolean {
        return when (route) {
            is ProfileRoute -> {
                ProfileScreen(navigationManager)
                true
            }
            else -> false
        }
    }
}
