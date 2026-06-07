package com.ytapps.composetemplate.feature.splash.navigation

import com.ytapps.composetemplate.core.navigation.INavigationItem
import kotlinx.serialization.Serializable

@Serializable
data object SplashRoute : INavigationItem {
    override val route: String = "route_splash"
}
