package com.lhacenmed.budget.feature.list.navigation

import com.lhacenmed.budget.core.navigation.INavigationItem
import kotlinx.serialization.Serializable

/**
 * Created by mustafayigitt on 02/12/2025
 * mustafa.yt65@gmail.com
 */

@Serializable
data object ListRoute : INavigationItem {
    override val route: String = "route_list"
}
