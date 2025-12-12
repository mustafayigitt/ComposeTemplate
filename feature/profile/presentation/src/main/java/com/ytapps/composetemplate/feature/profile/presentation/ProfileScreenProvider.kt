package com.ytapps.composetemplate.feature.profile.presentation

import androidx.compose.runtime.Composable
import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.navigation.IScreenProvider
import com.ytapps.composetemplate.feature.profile.navigation.ProfileRoute
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
