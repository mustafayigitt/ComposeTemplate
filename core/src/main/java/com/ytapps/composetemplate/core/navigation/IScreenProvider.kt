package com.ytapps.composetemplate.core.navigation

import androidx.compose.runtime.Composable

/**
 * Interface for providing screens for navigation routes.
 * Each feature's presentation module should implement this interface
 * to provide screens for its routes.
 */
interface IScreenProvider {
    /**
     * Provides the screen composable for the given route.
     * Returns null if this provider doesn't handle the given route.
     */
    @Composable
    fun provideScreen(route: INavigationItem, navigationManager: INavigationManager): Boolean
}
