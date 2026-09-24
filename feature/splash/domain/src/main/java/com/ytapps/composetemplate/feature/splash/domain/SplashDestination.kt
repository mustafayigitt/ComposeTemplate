package com.ytapps.composetemplate.feature.splash.domain

sealed interface SplashDestination {
    data object Onboarding : SplashDestination

    data object Home : SplashDestination
}
