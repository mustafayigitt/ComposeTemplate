package com.ytapps.composetemplate.feature.auth.navigation

import com.ytapps.composetemplate.core.navigation.INavigationItem
import kotlinx.serialization.Serializable

/**
 * Created by mustafayigitt on 26/08/2023
 * mustafa.yt65@gmail.com
 */

@Serializable
data object LoginRoute : INavigationItem {
    override val route: String = "route_login"
}
