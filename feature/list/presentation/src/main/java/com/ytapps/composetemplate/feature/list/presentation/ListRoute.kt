package com.ytapps.composetemplate.feature.list.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.ui.components.AppEmptyState
import com.ytapps.composetemplate.core.ui.components.AppListItem
import com.ytapps.composetemplate.core.ui.components.AppTopBar
import com.ytapps.composetemplate.feature.detail.navigation.DetailRoute

@Composable
fun ListScreen(navigationManager: INavigationManager) {
    ListScreenInternal(
        navigationManager = navigationManager,
    )
}

@Composable
internal fun ListScreenInternal(
    navigationManager: INavigationManager,
    viewModel: ListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.getItems()
    }

    ListScreenContent(
        uiState = uiState,
        onItemClick = { itemId ->
            navigationManager.navigate(DetailRoute(itemId))
        },
    )
}

@Composable
internal fun ListScreenContent(
    uiState: ListUiState,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        AppTopBar(title = "Template Modules")

        if (uiState.items.isEmpty()) {
            AppEmptyState(message = "No modules available")
            return@Column
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    horizontal = 16.dp,
                    vertical = 8.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                items = uiState.items,
                key = { it.id },
            ) { item ->
                AppListItem(
                    title = item.title,
                    subtitle = item.subtitle,
                    onClick = { onItemClick(item.id) },
                )
            }
        }
    }
}
