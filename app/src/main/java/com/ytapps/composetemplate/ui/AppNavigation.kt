package com.ytapps.composetemplate.ui

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.ytapps.composetemplate.core.analytics.AnalyticsEvent
import com.ytapps.composetemplate.core.analytics.IAnalyticsManager
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.navigation.ScreenRegistry
import com.ytapps.composetemplate.core.network.NetworkMonitor
import com.ytapps.composetemplate.core.network.NetworkStatus
import com.ytapps.composetemplate.core.ui.components.AppNoInternetBanner
import com.ytapps.composetemplate.core.ui.theme.component.AppNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navigationManager: INavigationManager,
    screenRegistry: ScreenRegistry,
    networkMonitor: NetworkMonitor,
    analyticsManager: IAnalyticsManager,
) {
    val backStack by navigationManager.backStack.collectAsStateWithLifecycle()
    val currentRoute = backStack.lastOrNull()
    val context = LocalContext.current

    val networkStatus by networkMonitor.networkStatus.collectAsStateWithLifecycle(initialValue = NetworkStatus.Available)

    LaunchedEffect(currentRoute) {
        currentRoute?.let {
            analyticsManager.logEvent(
                AnalyticsEvent(
                    type = AnalyticsEvent.SCREEN_VIEW,
                    extras = mapOf(AnalyticsEvent.SCREEN_NAME to it.route),
                ),
            )
        }
    }

    if (currentRoute != null) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                AppNoInternetBanner(isVisible = networkStatus != NetworkStatus.Available)
            },
            bottomBar = {
                if (navigationManager.showBottomBar(currentRoute)) {
                    AppNavigationBar(
                        currentRoute = currentRoute,
                        items = navigationManager.bottomBarItems,
                        onItemClick = { selected ->
                            navigationManager.selectTab(selected)
                        },
                    )
                }
            },
        ) { paddingValues ->
            NavDisplay(
                modifier = Modifier.padding(paddingValues),
                backStack = backStack,
                onBack = {
                    val handled = navigationManager.navigateBack()
                    if (!handled) {
                        (context as? Activity)?.finish()
                    }
                },
                entryProvider = { key ->
                    NavEntry(key) {
                        screenRegistry.ScreenProvider(key, navigationManager)
                    }
                },
            )
        }
    }
}
