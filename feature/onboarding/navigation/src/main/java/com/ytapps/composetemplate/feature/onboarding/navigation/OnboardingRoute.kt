package com.ytapps.composetemplate.feature.onboarding.navigation

import com.ytapps.composetemplate.core.navigation.INavigationItem
import kotlinx.serialization.Serializable

@Serializable
data object OnboardingRoute : INavigationItem {
    override val route: String = "route_onboarding"
}
