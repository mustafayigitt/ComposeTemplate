package com.ytapps.composetemplate.presentation.login

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
import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.core.navigation.NavigationManager
import com.ytapps.composetemplate.presentation.splash.Splash
import kotlinx.serialization.Serializable

/**
 * Created by mustafayigitt on 26/08/2023
 * mustafa.yt65@gmail.com
 */
@Serializable
data object Login : INavigationItem {
    override val route: String = "route_login"

    @Composable
    override fun ContentScreen(navigationManager: NavigationManager) {
        LoginScreen(
            navigationManager = navigationManager
        )
    }
}


@Composable
fun LoginScreen(
    navigationManager: NavigationManager,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        uiState.shouldNavigateToSplash.let { shouldNavigateToSplash ->
            if (shouldNavigateToSplash) {
                navigationManager.navigateOver(
                    route = Splash,
                    over = Login
                )
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