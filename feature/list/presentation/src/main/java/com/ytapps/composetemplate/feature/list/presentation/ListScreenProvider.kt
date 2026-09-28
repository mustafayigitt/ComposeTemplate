package com.ytapps.composetemplate.feature.list.presentation

import androidx.compose.runtime.Composable
import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.navigation.IScreenProvider
import com.ytapps.composetemplate.feature.list.navigation.ListRoute
import javax.inject.Inject

class ListScreenProvider
    @Inject
    constructor() : IScreenProvider {
        override fun restoreRoute(route: String): INavigationItem? = ListRoute.takeIf { it.route == route }

        @Composable
        override fun provideScreen(
            route: INavigationItem,
            navigationManager: INavigationManager,
        ): Boolean =
            when (route) {
                is ListRoute -> {
                    ListScreen(navigationManager)
                    true
                }

                else -> false
            }
    }
