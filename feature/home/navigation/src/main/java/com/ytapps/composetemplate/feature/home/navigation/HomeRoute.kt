package com.ytapps.composetemplate.feature.home.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.ytapps.composetemplate.core.navigation.IBottomBarItem
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute : IBottomBarItem {
    override val route: String = "route_home"

    override val icon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Home",
        )
    }
}
