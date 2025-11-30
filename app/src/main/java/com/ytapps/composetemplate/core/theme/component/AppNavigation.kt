package com.ytapps.composetemplate.core.theme.component

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.ytapps.composetemplate.MainActivity
import com.ytapps.composetemplate.core.navigation.NavigationManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navigationManager = rememberNavigationManager()
    val backStack by navigationManager.backStack.collectAsStateWithLifecycle()
    val currentRoute = backStack.lastOrNull()

    if (currentRoute != null) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (navigationManager.showBottomBar(currentRoute)) {
                    AppNavigationBar(
                        currentRoute = currentRoute,
                        items = navigationManager.bottomBarItems,
                        onItemClick = { selected ->
                            navigationManager.selectTab(selected)
                        }
                    )
                }
            }
        ) { paddingValues ->
            NavDisplay(
                modifier = Modifier.padding(paddingValues),
                backStack = backStack,
                onBack = {
                    navigationManager.navigateBack()
                },
                entryProvider = { key ->
                    NavEntry(key) {
                        key.ContentScreen(navigationManager)
                    }
                }
            )
        }
    } else {
        (LocalActivity.current as? MainActivity)?.finish()
    }
}

@Composable
fun rememberNavigationManager(): NavigationManager {
    return remember(Unit) { NavigationManager() }
}
