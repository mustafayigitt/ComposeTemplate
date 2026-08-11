package com.ytapps.composetemplate.feature.list.navigation

import com.ytapps.composetemplate.core.navigation.INavigationItem
import kotlinx.serialization.Serializable

@Serializable
data object ListRoute : INavigationItem {
    override val route: String = "route_list"
}
