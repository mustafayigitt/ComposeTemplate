package com.ytapps.composetemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ytapps.composetemplate.core.analytics.IAnalyticsManager
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.navigation.ScreenRegistry
import com.ytapps.composetemplate.core.network.NetworkMonitor
import com.ytapps.composetemplate.core.ui.theme.ComposeTemplateTheme
import com.ytapps.composetemplate.ui.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigationManager: INavigationManager

    @Inject
    lateinit var screenRegistry: ScreenRegistry

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var analyticsManager: IAnalyticsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by navigationManager.isDarkModeFlow.collectAsStateWithLifecycle()

            ComposeTemplateTheme(darkTheme = isDarkMode) {
                AppNavigation(
                    navigationManager = navigationManager,
                    screenRegistry = screenRegistry,
                    networkMonitor = networkMonitor,
                    analyticsManager = analyticsManager,
                    isDarkMode = isDarkMode,
                )
            }
        }
    }
}
