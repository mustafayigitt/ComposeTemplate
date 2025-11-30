package com.ytapps.composetemplate.presentation.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ytapps.composetemplate.core.navigation.IBottomBarItem
import com.ytapps.composetemplate.core.navigation.NavigationManager
import kotlinx.serialization.Serializable

/**
 * Created by mustafayigitt on 25/08/2023
 * mustafa.yt65@gmail.com
 */
@Serializable
data object Profile : IBottomBarItem {
    override val route: String = "route_profile"
    override val icon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "Profile"
        )
    }

    @Composable
    override fun ContentScreen(navigationManager: NavigationManager) {
        ProfileScreen(
            navigationManager = navigationManager
        )
    }
}

@Composable
fun ProfileScreen(
    navigationManager: NavigationManager,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Profile Screen")
    }
}