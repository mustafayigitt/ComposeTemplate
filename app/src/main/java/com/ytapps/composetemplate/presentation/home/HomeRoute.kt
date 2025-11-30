package com.ytapps.composetemplate.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ytapps.composetemplate.core.navigation.IBottomBarItem
import com.ytapps.composetemplate.core.navigation.NavigationManager
import com.ytapps.composetemplate.presentation.list.List
import kotlinx.serialization.Serializable

/**
 * Created by mustafa.yigit on 25/08/2023
 * mustafa.yt65@gmail.com
 */
@Serializable
data object Home : IBottomBarItem {
    override val route: String = "route_home"
    override val icon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Filled.Home,
            contentDescription = "Home"
        )
    }

    @Composable
    override fun ContentScreen(navigationManager: NavigationManager) {
        HomeScreen(
            navigationManager = navigationManager
        )
    }
}

@Composable
fun HomeScreen(
    navigationManager: NavigationManager,
    viewModel: HomeViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Home Screen")
        Button(
            onClick = {
                navigationManager.navigate(List)
            }
        ) {
            Text(text = "Go to List")
        }
    }
}