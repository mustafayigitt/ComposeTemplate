package com.ytapps.composetemplate.core.navigation

import androidx.compose.runtime.Composable

/**
 * Provides rendering and restoration for routes owned by one feature.
 */
interface IScreenProvider {
    /** Recreates a typed route from its stable route string after process recreation. */
    fun restoreRoute(route: String): INavigationItem? = null

    /** Returns false when this provider does not handle the route. */
    @Composable
    fun provideScreen(
        route: INavigationItem,
        navigationManager: INavigationManager,
    ): Boolean
}
