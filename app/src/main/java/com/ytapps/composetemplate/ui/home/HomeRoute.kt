package com.ytapps.composetemplate.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.ytapps.composetemplate.core.navigation.IBottomBarItem

/**
 * Created by mustafa.yigit on 25/08/2023
 * mustafa.yt65@gmail.com
 */

fun NavGraphBuilder.homeGraph(
    navController: NavHostController
) {
    composable(Home.route) {
        HomeScreen(
            navController = navController
        )
    }
}

data object Home : IBottomBarItem {
    override val route: String = "route_home"
    override val icon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Filled.Home,
            contentDescription = "Home"
        )
    }
}

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Home Screen")
    }
}