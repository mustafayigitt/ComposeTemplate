package com.ytapps.composetemplate.feature.detail.navigation

import com.ytapps.composetemplate.core.navigation.INavigationItem
import kotlinx.serialization.Serializable

@Serializable
data class DetailRoute(
    val id: String,
) : INavigationItem {
    override val route: String = "route_detail/$id"
}
