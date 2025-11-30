package com.ytapps.composetemplate.presentation.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.core.navigation.NavigationManager
import kotlinx.serialization.Serializable

/**
 * Created by mustafa.yigit on 25/08/2023
 * mustafa.yt65@gmail.com
 */
@Serializable
data class Detail(
    val id: String
) : INavigationItem {
    override val route: String = "route_detail"

    @Composable
    override fun ContentScreen(navigationManager: NavigationManager) {
        DetailScreen(
            id = id,
            navigationManager = navigationManager
        )
    }
}

@Composable
fun DetailScreen(
    navigationManager: NavigationManager,
    id: String,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Detail id: ${uiState.id}")

        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    start = 16.dp,
                    top = 16.dp
                ),
            onClick = {
                navigationManager.navigateBack()
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
            )
        }

    }

    LaunchedEffect(id) {
        viewModel.setDetailId(id)
    }
}