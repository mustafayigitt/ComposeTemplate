package com.ytapps.composetemplate.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.core.navigation.NavigationManager
import com.ytapps.composetemplate.presentation.detail.Detail
import kotlinx.serialization.Serializable

/**
 * Created by mustafayigitt on 02/12/2025
 * mustafa.yt65@gmail.com
 */
@Serializable
data object List : INavigationItem {
    override val route: String = "route_list"

    @Composable
    override fun ContentScreen(navigationManager: NavigationManager) {
        ListScreen(
            navigationManager = navigationManager
        )
    }
}

@Composable
fun ListScreen(
    navigationManager: NavigationManager,
    viewModel: ListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(uiState.items) {
            Button(
                onClick = {
                    navigationManager.navigate(Detail(it))
                }
            ) {
                Text(
                    text = "Item $it",
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getItems()
    }
}