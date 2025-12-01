package com.ytapps.composetemplate.presentation.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.ytapps.composetemplate.core.navigation.IBottomBarItem
import com.ytapps.composetemplate.core.navigation.NavigationManager
import kotlinx.serialization.Serializable

/**
 * Created by mustafayigitt on 02/12/2025
 * mustafa.yt65@gmail.com
 */
@Serializable
data object Search : IBottomBarItem {
    override val route: String = "route_search"
    override val icon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "Search"
        )
    }

    @Composable
    override fun ContentScreen(navigationManager: NavigationManager) {
        SearchScreen(
            navigationManager = navigationManager
        )
    }
}

@Composable
fun SearchScreen(
    navigationManager: NavigationManager,
    viewModel: SearchViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Search Screen")
    }
}