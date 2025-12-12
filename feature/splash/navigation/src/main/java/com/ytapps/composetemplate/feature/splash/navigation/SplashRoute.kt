package com.ytapps.composetemplate.feature.splash.navigation

import com.ytapps.composetemplate.core.navigation.INavigationItem
import kotlinx.serialization.Serializable

/**
 * Created by mustafayigitt on 02/12/2025
 * mustafa.yt65@gmail.com
 */

@Serializable
data object SplashRoute : INavigationItem {
    override val route: String = "route_splash"
}
