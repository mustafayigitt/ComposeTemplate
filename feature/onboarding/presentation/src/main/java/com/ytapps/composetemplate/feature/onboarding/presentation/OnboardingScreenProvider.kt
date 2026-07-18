package com.ytapps.composetemplate.feature.onboarding.presentation

import androidx.compose.runtime.Composable
import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.navigation.IScreenProvider
import com.ytapps.composetemplate.feature.onboarding.navigation.OnboardingRoute
import javax.inject.Inject

class OnboardingScreenProvider
    @Inject
    constructor() : IScreenProvider {
        @Composable
        override fun provideScreen(
            route: INavigationItem,
            navigationManager: INavigationManager,
        ): Boolean =
            when (route) {
                is OnboardingRoute -> {
                    OnboardingScreen(navigationManager)
                    true
                }
                else -> false
            }
    }
