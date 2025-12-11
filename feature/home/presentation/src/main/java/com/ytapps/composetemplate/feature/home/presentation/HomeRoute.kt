package com.ytapps.composetemplate.feature.home.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ytapps.composetemplate.contract.ListRoute
import com.ytapps.composetemplate.core.navigation.INavigationManager

/**
 * Created by mustafayigitt on 02/12/2025
 * mustafa.yt65@gmail.com
 */
@Composable
fun HomeScreen(
    navigationManager: INavigationManager,
) {
    HomeScreenInternal(
        navigationManager = navigationManager
    )
}

@Composable
internal fun HomeScreenInternal(
    navigationManager: INavigationManager,
    viewModel: HomeViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Home Screen")
        Button(
            onClick = {
                navigationManager.navigate(ListRoute)
            }
        ) {
            Text(text = "Go to List")
        }
    }
}