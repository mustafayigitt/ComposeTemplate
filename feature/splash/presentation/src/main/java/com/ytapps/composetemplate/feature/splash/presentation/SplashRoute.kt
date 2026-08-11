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
import com.ytapps.composetemplate.feature.splash.navigation.SplashRoute

@Composable
fun SplashScreen(navigationManager: INavigationManager) {
    SplashScreenInternal(
        navigationManager = navigationManager,
    )
}

@Composable
internal fun SplashScreenInternal(
    navigationManager: INavigationManager,
    viewModel: SplashViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.checkDestination()

        viewModel.events.collect { event ->
            when (event) {
                is SplashEvent.NavigateTo -> navigationManager.navigateOver(event.route, over = SplashRoute)
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Splash Screen")
    }
}
