package com.ytapps.composetemplate.feature.auth.presentation

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
import com.ytapps.composetemplate.contract.LoginRoute
import com.ytapps.composetemplate.contract.SplashRoute
import com.ytapps.composetemplate.core.navigation.INavigationManager

/**
 * Created by mustafayigitt on 26/08/2023
 * mustafa.yt65@gmail.com
 */

@Composable
fun LoginScreen(
    navigationManager: INavigationManager,
) {
    LoginScreenInternal(
        navigationManager = navigationManager
    )
}

@Composable
internal fun LoginScreenInternal(
    navigationManager: INavigationManager,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        uiState.shouldNavigateToSplash.let { shouldNavigateToSplash ->
            if (shouldNavigateToSplash) {
                navigationManager.navigateOver(
                    route = SplashRoute,
                    over = LoginRoute
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