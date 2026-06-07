package com.ytapps.composetemplate.feature.profile.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import com.ytapps.composetemplate.core.navigation.IBottomBarItem
import kotlinx.serialization.Serializable

@Serializable
data object ProfileRoute : IBottomBarItem {
    override val route: String = "route_profile"

    override val icon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile",
        )
    }
}
