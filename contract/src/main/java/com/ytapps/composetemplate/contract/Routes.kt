package com.ytapps.composetemplate.contract

import androidx.compose.material3.Icon
import com.ytapps.composetemplate.core.navigation.IBottomBarItem
import com.ytapps.composetemplate.core.navigation.INavigationItem
import kotlinx.serialization.Serializable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable

@Serializable
data object SplashRoute : INavigationItem {
    override val route: String = "route_splash"
}

@Serializable
data object LoginRoute : INavigationItem {
    override val route: String = "route_login"
}

@Serializable
data object HomeRoute : IBottomBarItem {
    override val route: String = "route_home"
    override val icon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Home"
        )
    }
}

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

@Serializable
data object ProfileRoute : IBottomBarItem {
    override val route: String = "route_profile"
    override val icon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile"
        )
    }
}

@Serializable
data object ListRoute : INavigationItem {
    override val route: String = "route_list"
}

@Serializable
data class DetailRoute(val id: String) : INavigationItem {
    override val route: String = "route_detail/$id"
}
