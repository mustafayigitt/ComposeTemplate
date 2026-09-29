package com.ytapps.composetemplate.core.navigation

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

internal class NavigationManagerTest {
    private lateinit var navigationManager: NavigationManager

    @Before
    fun setUp() {
        navigationManager =
            NavigationManager(
                startDestination = TestRoute.Home,
                bottomBarItemsRaw =
                    mapOf(
                        "1" to TestRoute.Home,
                        "2" to TestRoute.Search,
                    ),
            )
    }

    @Test
    fun `given initial state then back stack contains only start destination`() {
        assertThat(navigationManager.backStack.value).containsExactly(TestRoute.Home)
    }

    @Test
    fun `given saved routes then restoreBackStack preserves history`() {
        navigationManager.restoreBackStack(
            listOf(TestRoute.Home, TestRoute.Detail, TestRoute.Profile),
        )

        assertThat(navigationManager.backStack.value)
            .containsExactly(TestRoute.Home, TestRoute.Detail, TestRoute.Profile)
            .inOrder()
    }

    @Test
    fun `given saved routes without root then restoreBackStack prepends root`() {
        navigationManager.restoreBackStack(listOf(TestRoute.Detail))

        assertThat(navigationManager.backStack.value)
            .containsExactly(TestRoute.Home, TestRoute.Detail)
            .inOrder()
    }

    @Test
    fun `given single item when navigate then route is added to stack`() {
        navigationManager.navigate(TestRoute.Detail)

        assertThat(navigationManager.backStack.value)
            .containsExactly(TestRoute.Home, TestRoute.Detail)
            .inOrder()
    }

    @Test
    fun `given multiple items when navigateBack then last item is removed`() {
        navigationManager.navigate(TestRoute.Detail)
        navigationManager.navigate(TestRoute.Profile)

        val navigated = navigationManager.navigateBack()

        assertThat(navigated).isTrue()
        assertThat(navigationManager.backStack.value)
            .containsExactly(TestRoute.Home, TestRoute.Detail)
            .inOrder()
    }

    @Test
    fun `given single item when navigateBack then returns false`() {
        assertThat(navigationManager.navigateBack()).isFalse()
        assertThat(navigationManager.backStack.value).containsExactly(TestRoute.Home)
    }

    @Test
    fun `given tab already in stack then selection truncates to tab`() {
        navigationManager.navigate(TestRoute.Detail)
        navigationManager.selectTab(TestRoute.Home)

        assertThat(navigationManager.backStack.value).containsExactly(TestRoute.Home)
    }

    @Test
    fun `given new tab then selection appends tab`() {
        navigationManager.selectTab(TestRoute.Search)

        assertThat(navigationManager.backStack.value)
            .containsExactly(TestRoute.Home, TestRoute.Search)
            .inOrder()
    }

    @Test
    fun `given existing target then navigateOver replaces from target`() {
        navigationManager.navigate(TestRoute.Detail)
        navigationManager.navigate(TestRoute.Profile)

        navigationManager.navigateOver(TestRoute.Search, TestRoute.Detail)

        assertThat(navigationManager.backStack.value)
            .containsExactly(TestRoute.Home, TestRoute.Search)
            .inOrder()
    }

    @Test
    fun `given route then navigateToTop resets above root`() {
        navigationManager.navigate(TestRoute.Detail)
        navigationManager.navigateToTop(TestRoute.Search)

        assertThat(navigationManager.backStack.value)
            .containsExactly(TestRoute.Home, TestRoute.Search)
            .inOrder()
    }

    @Test
    fun `bottom bar visibility uses registered items`() {
        assertThat(navigationManager.showBottomBar(TestRoute.Home)).isTrue()
        assertThat(navigationManager.showBottomBar(TestRoute.Detail)).isFalse()
    }

    @Test
    fun `bottom bar items are sorted by key`() {
        assertThat(navigationManager.bottomBarItems)
            .containsExactly(TestRoute.Home, TestRoute.Search)
            .inOrder()
    }

    @Test
    fun `given nested stack then navigateBackToRoot resets stack`() {
        navigationManager.navigate(TestRoute.Detail)

        assertThat(navigationManager.navigateBackToRoot()).isTrue()
        assertThat(navigationManager.backStack.value).containsExactly(TestRoute.Home)
    }

    @Test
    fun `backStack state flow emits updates`() =
        runTest {
            navigationManager.navigate(TestRoute.Detail)

            assertThat(navigationManager.backStack.first())
                .containsExactly(TestRoute.Home, TestRoute.Detail)
                .inOrder()
        }

    private sealed interface TestRoute : INavigationItem {
        data object Home : TestRoute, IBottomBarItem {
            override val route = "home"
            override val icon: @androidx.compose.runtime.Composable () -> Unit = {}
        }

        data object Search : TestRoute, IBottomBarItem {
            override val route = "search"
            override val icon: @androidx.compose.runtime.Composable () -> Unit = {}
        }

        data object Detail : TestRoute {
            override val route = "detail"
        }

        data object Profile : TestRoute {
            override val route = "profile"
        }
    }
}
