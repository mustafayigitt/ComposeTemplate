package com.ytapps.composetemplate.feature.splash.presentation

import com.ytapps.composetemplate.core.navigation.INavigationItem

sealed interface SplashEvent {
    data class NavigateTo(val route: INavigationItem) : SplashEvent
}
