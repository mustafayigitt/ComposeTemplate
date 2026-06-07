package com.ytapps.composetemplate.feature.splash.domain

sealed interface SplashDestination {
    data object Login : SplashDestination

    data object Home : SplashDestination
}
