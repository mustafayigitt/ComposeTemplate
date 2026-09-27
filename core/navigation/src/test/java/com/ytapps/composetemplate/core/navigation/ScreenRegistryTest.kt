package com.ytapps.composetemplate.core.navigation

import androidx.compose.runtime.Composable
import com.google.common.truth.Truth.assertThat
import org.junit.Test

internal class ScreenRegistryTest {
    @Test
    fun `restoreBackStack recreates known routes and ignores removed routes`() {
        val registry = ScreenRegistry(setOf(TestProvider()))

        val restored = registry.restoreBackStack(listOf("home", "removed", "detail/42"))

        assertThat(restored)
            .containsExactly(TestRoute.Home, TestRoute.Detail("42"))
            .inOrder()
    }

    private class TestProvider : IScreenProvider {
        override fun restoreRoute(route: String): INavigationItem? =
            when {
                route == TestRoute.Home.route -> TestRoute.Home
                route.startsWith("detail/") -> TestRoute.Detail(route.removePrefix("detail/"))
                else -> null
            }

        @Composable
        override fun provideScreen(
            route: INavigationItem,
            navigationManager: INavigationManager,
        ): Boolean = false
    }

    private sealed interface TestRoute : INavigationItem {
        data object Home : TestRoute {
            override val route = "home"
        }

        data class Detail(
            val id: String,
        ) : TestRoute {
            override val route = "detail/$id"
        }
    }
}
