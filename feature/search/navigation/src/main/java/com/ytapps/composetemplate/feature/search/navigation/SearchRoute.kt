package com.ytapps.composetemplate.feature.search.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.ytapps.composetemplate.core.navigation.IBottomBarItem
import kotlinx.serialization.Serializable

/**
 * Created by mustafayigitt on 02/12/2025
 * mustafa.yt65@gmail.com
 */

@Serializable
data object SearchRoute : IBottomBarItem {
    override val route: String = "route_search"
    
    override val icon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search"
        )
    }
}
