package com.ytapps.composetemplate.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.feature.list.navigation.ListRoute

@Composable
fun HomeScreen(navigationManager: INavigationManager) {
    HomeScreenInternal(
        navigationManager = navigationManager,
    )
}

@Composable
internal fun HomeScreenInternal(
    navigationManager: INavigationManager,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "Home Screen")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navigationManager.navigate(ListRoute)
            },
        ) {
            Text(text = "Go to List")
        }
    }
}
