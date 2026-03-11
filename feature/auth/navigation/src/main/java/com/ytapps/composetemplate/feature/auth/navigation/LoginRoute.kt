package com.lhacenmed.budget.feature.auth.navigation

import com.lhacenmed.budget.core.navigation.INavigationItem
import kotlinx.serialization.Serializable

/**
 * Created by mustafayigitt on 26/08/2023
 * mustafa.yt65@gmail.com
 */

@Serializable
data object LoginRoute : INavigationItem {
    override val route: String = "route_login"
}
