package com.ytapps.composetemplate.feature.search.presentation

import androidx.compose.runtime.Composable
import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.navigation.IScreenProvider
import com.ytapps.composetemplate.feature.search.navigation.SearchRoute
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
