package com.ytapps.composetemplate.core.navigation

import androidx.compose.runtime.Composable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Registry that provides screens for routes using multibound IScreenProvider instances.
 */
@Singleton
class ScreenRegistry @Inject constructor(
    private val screenProviders: Set<@JvmSuppressWildcards IScreenProvider>
) {
    @Composable
    fun ScreenProvider(route: INavigationItem, navigationManager: INavigationManager) {
        // Try each provider until one handles the route
        for (provider in screenProviders) {
            if (provider.provideScreen(route, navigationManager)) {
                return
            }
        }
        // If no provider handles the route, show nothing (or could show error screen)
    }
}
