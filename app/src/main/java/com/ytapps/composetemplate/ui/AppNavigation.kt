package com.ytapps.composetemplate.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.ytapps.composetemplate.MainActivity
import com.ytapps.composetemplate.contract.DetailRoute
import com.ytapps.composetemplate.contract.HomeRoute
import com.ytapps.composetemplate.contract.ListRoute
import com.ytapps.composetemplate.contract.LoginRoute
import com.ytapps.composetemplate.contract.ProfileRoute
import com.ytapps.composetemplate.contract.SearchRoute
import com.ytapps.composetemplate.contract.SplashRoute
import com.ytapps.composetemplate.core.navigation.NavigationManager
import com.ytapps.composetemplate.core.theme.component.AppNavigationBar
import com.ytapps.composetemplate.feature.auth.presentation.LoginScreen
import com.ytapps.composetemplate.feature.detail.presentation.DetailScreen
import com.ytapps.composetemplate.feature.home.presentation.HomeScreen
import com.ytapps.composetemplate.feature.list.presentation.ListScreen
import com.ytapps.composetemplate.feature.profile.presentation.ProfileScreen
import com.ytapps.composetemplate.feature.search.presentation.SearchScreen
import com.ytapps.composetemplate.feature.splash.presentation.SplashScreen
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    navigationManager: NavigationManager
) {
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
                        when(key) {
                            is SplashRoute -> SplashScreen(navigationManager)
                            is LoginRoute -> LoginScreen(navigationManager)
                            is HomeRoute -> HomeScreen(navigationManager)
                            is ListRoute -> ListScreen(navigationManager)
                            is SearchRoute -> SearchScreen(navigationManager)
                            is ProfileRoute -> ProfileScreen(navigationManager)
                            is DetailRoute -> DetailScreen(navigationManager, key.id)
                            else -> {}
                        }
                    }
                }
            )
        }
    } else {
        (LocalActivity.current as? MainActivity)?.finish()
    }
}
