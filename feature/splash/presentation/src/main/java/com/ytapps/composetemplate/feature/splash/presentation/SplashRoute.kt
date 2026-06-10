package com.ytapps.composetemplate.feature.splash.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ytapps.composetemplate.core.navigation.INavigationManager

@Composable
fun SplashScreen(navigationManager: INavigationManager) {
    SplashScreenInternal(
        navigationManager = navigationManager,
    )
}

/*** Screen */
@Composable
internal fun SplashScreenInternal(
    navigationManager: INavigationManager,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        uiState.destinationRoute?.let { destinationRoute ->
            navigationManager.navigateToTop(destinationRoute)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Splash Screen")
    }
}