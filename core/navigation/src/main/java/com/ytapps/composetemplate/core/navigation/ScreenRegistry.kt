package com.ytapps.composetemplate.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScreenRegistry @Inject constructor(
    private val screenProviders: Set<@JvmSuppressWildcards IScreenProvider>,
) {
    @Composable
    fun ScreenProvider(
        route: INavigationItem,
        navigationManager: INavigationManager,
    ) {
        for (provider in screenProviders) {
            if (provider.provideScreen(route, navigationManager)) {
                return
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "Screen not found: ${route.route}")
        }
    }
}
