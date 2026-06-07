package com.ytapps.composetemplate.feature.search.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ytapps.composetemplate.core.navigation.INavigationManager

@Composable
fun SearchScreen(navigationManager: INavigationManager) {
    SearchScreenInternal(navigationManager = navigationManager)
}

@Composable
internal fun SearchScreenInternal(navigationManager: INavigationManager) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Search Screen")
    }
}
