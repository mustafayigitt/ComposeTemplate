package com.ytapps.composetemplate.feature.splash.presentation

import androidx.compose.runtime.Composable
import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.navigation.IScreenProvider
import com.ytapps.composetemplate.feature.splash.navigation.SplashRoute
import javax.inject.Inject

class SplashScreenProvider
    @Inject
    constructor() : IScreenProvider {
        override fun restoreRoute(route: String): INavigationItem? =
            SplashRoute.takeIf { it.route == route }

        @Composable
        override fun provideScreen(
            route: INavigationItem,
            navigationManager: INavigationManager,
        ): Boolean =
            when (route) {
                is SplashRoute -> {
                    SplashScreen(navigationManager)
                    true
                }

                else -> false
            }
    }
