package com.ytapps.composetemplate.feature.detail.presentation

import androidx.compose.runtime.Composable
import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.navigation.IScreenProvider
import com.ytapps.composetemplate.feature.detail.navigation.DetailRoute
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
