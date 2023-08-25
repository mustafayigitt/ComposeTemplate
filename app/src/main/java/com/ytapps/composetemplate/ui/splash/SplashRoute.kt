package com.ytapps.composetemplate.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ytapps.composetemplate.ui.navigation.INavigationItem

/**
 * Created by mustafayigitt on 25/08/2023
 * mustafa.yt65@gmail.com
 */

/*** Navigation */
fun NavGraphBuilder.splashGraph(
    navController: NavController
) {
    composable(Splash.route) {
        SplashScreen(
            navController = navController
        )
    }
}

data object Splash : INavigationItem {
    override val route: String = "route_splash"
}

/*** Screen */
@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        uiState.destinationRoute?.let { destinationRoute ->
            navController.navigate(destinationRoute) {
                popUpTo(navController.graph.startDestinationRoute!!) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Splash Screen")
    }
}