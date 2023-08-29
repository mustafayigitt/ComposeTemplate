package com.ytapps.composetemplate.ui.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
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
import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.ui.splash.Splash

/**
 * Created by mustafa.yigit on 26/08/2023
 * mustafa.yt65@gmail.com
 */

fun NavGraphBuilder.loginRoute(navController: NavController) {
    composable(Login.route) {
        LoginScreen(
            navController = navController
        )
    }
}

data object Login : INavigationItem {
    override val route: String = "route_login"
}


@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        uiState.shouldNavigateToSplash.let { shouldNavigateToSplash ->
            if (shouldNavigateToSplash) {
                navController.navigate(Splash.route) {
                    popUpTo(Login.route) {
                        inclusive = true
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                viewModel.login(uiState.email, uiState.password)
            }
        ) {
            Text(text = "Login")
        }
    }
}