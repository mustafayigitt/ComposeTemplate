package com.ytapps.composetemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ytapps.composetemplate.core.common.IThemeManager
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.navigation.NavigationObserver
import com.ytapps.composetemplate.core.navigation.ScreenRegistry
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
    lateinit var themeManager: IThemeManager

    @Inject
    lateinit var navigationObservers: Set<@JvmSuppressWildcards NavigationObserver>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState
            ?.getStringArrayList(NAVIGATION_BACK_STACK_KEY)
            ?.let(screenRegistry::restoreBackStack)
            ?.takeIf { it.isNotEmpty() }
            ?.let(navigationManager::restoreBackStack)

        enableEdgeToEdge()
        setContent {
            val isDarkMode by themeManager.isDarkModeFlow.collectAsStateWithLifecycle()

            ComposeTemplateTheme(darkTheme = isDarkMode) {
                AppNavigation(
                    navigationManager = navigationManager,
                    screenRegistry = screenRegistry,
                    navigationObservers = navigationObservers,
                )
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putStringArrayList(
            NAVIGATION_BACK_STACK_KEY,
            ArrayList(navigationManager.backStack.value.map { it.route }),
        )
        super.onSaveInstanceState(outState)
    }

    private companion object {
        const val NAVIGATION_BACK_STACK_KEY = "navigation_back_stack"
    }
}
