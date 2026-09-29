package com.ytapps.composetemplate.feature.home.presentation

import androidx.compose.runtime.Composable
import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.navigation.IScreenProvider
import com.ytapps.composetemplate.feature.home.navigation.HomeRoute
import javax.inject.Inject

class HomeScreenProvider
    @Inject
    constructor() : IScreenProvider {
        override fun restoreRoute(route: String): INavigationItem? = HomeRoute.takeIf { it.route == route }

        @Composable
        override fun provideScreen(
            route: INavigationItem,
            navigationManager: INavigationManager,
        ): Boolean =
            when (route) {
                is HomeRoute -> {
                    HomeScreen(navigationManager)
                    true
                }

                else -> false
            }
    }
