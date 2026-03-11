package com.lhacenmed.budget.feature.splash.navigation

import com.lhacenmed.budget.core.navigation.INavigationItem
import kotlinx.serialization.Serializable

/**
 * Created by mustafayigitt on 02/12/2025
 * mustafa.yt65@gmail.com
 */

@Serializable
data object SplashRoute : INavigationItem {
    override val route: String = "route_splash"
}
