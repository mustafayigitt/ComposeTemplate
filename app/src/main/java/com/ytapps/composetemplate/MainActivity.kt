package com.ytapps.composetemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ytapps.composetemplate.core.common.connectivity.NetworkMonitor
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.navigation.NavigationObserver
import com.ytapps.composetemplate.core.navigation.ScreenRegistry
import com.ytapps.composetemplate.core.ui.theme.ComposeTemplateTheme
import com.ytapps.composetemplate.ui.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Every dependency injected here comes from a module that is never deleted:
 * `core:navigation` and `core:common`. Optional modules reach the app through multibindings
 * instead of imports, which is what makes them removable.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigationManager: INavigationManager

    @Inject
    lateinit var screenRegistry: ScreenRegistry

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var navigationObservers: Set<@JvmSuppressWildcards NavigationObserver>

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
                    navigationObservers = navigationObservers,
                )
            }
        }
    }
}
