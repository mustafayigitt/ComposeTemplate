package com.ytapps.composetemplate.core.navigation

import androidx.compose.runtime.Composable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScreenRegistry
    @Inject
    constructor(
        private val screenProviders: Set<@JvmSuppressWildcards IScreenProvider>,
    ) {
        fun restoreBackStack(routes: List<String>): List<INavigationItem> =
            routes.mapNotNull { savedRoute ->
                val matches = screenProviders.mapNotNull { it.restoreRoute(savedRoute) }
                check(matches.size <= 1) {
                    "Multiple screen providers restored route '$savedRoute'."
                }
                matches.singleOrNull()
            }

        @Composable
        fun ScreenProvider(
            route: INavigationItem,
            navigationManager: INavigationManager,
        ) {
            for (provider in screenProviders) {
                if (provider.provideScreen(route, navigationManager)) return
            }
            error("No IScreenProvider registered for route '${route.route}'.")
        }
    }
