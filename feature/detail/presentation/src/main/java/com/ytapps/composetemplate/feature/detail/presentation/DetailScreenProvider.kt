package com.ytapps.composetemplate.feature.detail.presentation

import androidx.compose.runtime.Composable
import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.navigation.IScreenProvider
import com.ytapps.composetemplate.feature.detail.navigation.DetailRoute
import javax.inject.Inject

class DetailScreenProvider
    @Inject
    constructor() : IScreenProvider {
        override fun restoreRoute(route: String): INavigationItem? =
            route
                .takeIf { it.startsWith(ROUTE_PREFIX) }
                ?.removePrefix(ROUTE_PREFIX)
                ?.takeIf { it.isNotBlank() }
                ?.let(::DetailRoute)

        @Composable
        override fun provideScreen(
            route: INavigationItem,
            navigationManager: INavigationManager,
        ): Boolean =
            when (route) {
                is DetailRoute -> {
                    DetailScreen(navigationManager, route.id)
                    true
                }

                else -> false
            }

        private companion object {
            const val ROUTE_PREFIX = "route_detail/"
        }
    }
