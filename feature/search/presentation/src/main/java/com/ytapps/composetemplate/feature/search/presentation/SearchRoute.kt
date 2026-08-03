package com.ytapps.composetemplate.feature.search.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.ui.components.AppEmptyState
import com.ytapps.composetemplate.core.ui.components.AppListItem
import com.ytapps.composetemplate.core.ui.components.AppSearchField
import com.ytapps.composetemplate.core.ui.components.AppTopBar

@Composable
fun SearchScreen(navigationManager: INavigationManager) {
    SearchScreenInternal(navigationManager = navigationManager)
}

@Composable
internal fun SearchScreenInternal(
    navigationManager: INavigationManager,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SearchScreenContent(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChanged,
    )
}

@Composable
internal fun SearchScreenContent(
    uiState: SearchUiState,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        AppTopBar(title = "Search")

        AppSearchField(
            value = uiState.query,
            onValueChange = onQueryChange,
            modifier = Modifier.padding(horizontal = 16.dp),
            placeholder = "Search template capabilities",
        )

        if (uiState.results.isEmpty()) {
            AppEmptyState(message = "No matching capability found")
            return@Column
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                items = uiState.results,
                key = { it },
            ) { result ->
                AppListItem(title = result)
            }
        }
    }
}
