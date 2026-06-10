package com.ytapps.composetemplate.feature.auth.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.ui.CommonUiEvent
import com.ytapps.composetemplate.feature.auth.navigation.LoginRoute
import com.ytapps.composetemplate.feature.splash.navigation.SplashRoute

@Composable
fun LoginScreen(navigationManager: INavigationManager) {
    LoginScreenInternal(
        navigationManager = navigationManager,
    )
}

@Composable
internal fun LoginScreenInternal(
    navigationManager: INavigationManager,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        uiState.shouldNavigateToSplash.let { shouldNavigateToSplash ->
            if (shouldNavigateToSplash) {
                navigationManager.navigateOver(
                    route = SplashRoute,
                    over = LoginRoute,
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CommonUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is CommonUiEvent.Unauthorized -> { }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Button(
            onClick = { viewModel.login(uiState.email, uiState.password) },
            modifier = Modifier.align(Alignment.Center),
        ) {
            Text(text = "Login")
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}