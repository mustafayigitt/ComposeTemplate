package com.ytapps.androidsinglemoduletemplate.ui.profile

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
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.ytapps.androidsinglemoduletemplate.ui.navigation.IBottomBarItem

/**
 * Created by mustafayigitt on 25/08/2023
 * mustafa.yt65@gmail.com
 */

fun NavGraphBuilder.profileGraph(
    navController: NavHostController
) {
    composable(Profile.route) {
        ProfileScreen(
            navController = navController
        )
    }
}

data object Profile : IBottomBarItem {
    override val route: String = "route_profile"
    override val icon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = "Profile"
        )
    }
}

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Profile Screen")
    }
}