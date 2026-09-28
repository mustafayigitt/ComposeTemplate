package com.ytapps.composetemplate.feature.auth.presentation

import androidx.compose.runtime.Composable
import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.navigation.IScreenProvider
import com.ytapps.composetemplate.feature.auth.navigation.LoginRoute
import javax.inject.Inject

class AuthScreenProvider
    @Inject
    constructor() : IScreenProvider {
        override fun restoreRoute(route: String): INavigationItem? = LoginRoute.takeIf { it.route == route }

        @Composable
        override fun provideScreen(
            route: INavigationItem,
            navigationManager: INavigationManager,
        ): Boolean =
            when (route) {
                is LoginRoute -> {
                    LoginScreen(navigationManager)
                    true
                }

                else -> false
            }
    }
