package com.ytapps.composetemplate.feature.list.presentation

import androidx.compose.runtime.Composable
import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.navigation.IScreenProvider
import com.ytapps.composetemplate.feature.list.navigation.ListRoute
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
