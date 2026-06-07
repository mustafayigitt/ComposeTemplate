package com.ytapps.composetemplate.feature.auth.navigation

import com.ytapps.composetemplate.core.navigation.INavigationItem
import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute : INavigationItem {
    override val route: String = "route_login"
}
