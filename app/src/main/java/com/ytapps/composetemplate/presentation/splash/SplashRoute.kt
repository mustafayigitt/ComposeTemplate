package com.ytapps.composetemplate.presentation.splash

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
import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.core.navigation.NavigationManager
import kotlinx.serialization.Serializable

/**
 * Created by mustafayigitt on 25/08/2023
 * mustafa.yt65@gmail.com
 */

/*** Navigation */
@Serializable
data object Splash : INavigationItem {
    override val route: String = "route_splash"

    @Composable
    override fun ContentScreen(navigationManager: NavigationManager) {
        SplashScreen(
            navigationManager = navigationManager
        )
    }
}

/*** Screen */
@Composable
fun SplashScreen(
    navigationManager: NavigationManager,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        uiState.destinationRoute?.let { destinationRoute ->
            navigationManager.navigateToTop(destinationRoute)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Splash Screen")
    }
}