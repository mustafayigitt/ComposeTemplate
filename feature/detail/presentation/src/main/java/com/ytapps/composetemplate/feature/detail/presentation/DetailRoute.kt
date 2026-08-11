package com.ytapps.composetemplate.feature.detail.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.ui.components.AppCard
import com.ytapps.composetemplate.core.ui.components.AppTopBar

@Composable
fun DetailScreen(
    navigationManager: INavigationManager,
    id: String,
) {
    DetailScreenInternal(
        navigationManager = navigationManager,
        id = id,
    )
}

@Composable
internal fun DetailScreenInternal(
    navigationManager: INavigationManager,
    id: String,
    viewModel: DetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(id, viewModel) {
        viewModel.setDetailId(id)
    }

    DetailScreenContent(
        uiState = uiState,
        onBackClick = navigationManager::navigateBack,
    )
}

@Composable
internal fun DetailScreenContent(
    uiState: DetailUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        AppTopBar(
            title = uiState.title.ifBlank { "Detail" },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                    )
                }
            },
        )

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
        ) {
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = uiState.title.ifBlank { uiState.id },
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = uiState.description.ifBlank { "No detail available for ${uiState.id}." },
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
