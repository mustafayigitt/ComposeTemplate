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
                bottomBarItemsRaw = mapOf("1" to TestRoute.Home, "2" to TestRoute.Search),
            )
    }

    @Test fun `initial stack contains start destination`() = assertThat(navigationManager.backStack.value).containsExactly(TestRoute.Home)

    @Test
    fun `restore stack preserves recognized history`() {
        navigationManager.restoreBackStack(listOf(TestRoute.Home, TestRoute.Detail, TestRoute.Profile))
        assertThat(navigationManager.backStack.value).containsExactly(TestRoute.Home, TestRoute.Detail, TestRoute.Profile).inOrder()
    }

    @Test
    fun `restore stack prepends start destination when missing`() {
        navigationManager.restoreBackStack(listOf(TestRoute.Detail))
        assertThat(navigationManager.backStack.value).containsExactly(TestRoute.Home, TestRoute.Detail).inOrder()
    }

    @Test fun `navigate adds route`() { navigationManager.navigate(TestRoute.Detail); assertThat(navigationManager.backStack.value).containsExactly(TestRoute.Home, TestRoute.Detail).inOrder() }

    @Test fun `navigateBack removes last item`() { navigationManager.navigate(TestRoute.Detail); assertThat(navigationManager.navigateBack()).isTrue(); assertThat(navigationManager.backStack.value).containsExactly(TestRoute.Home) }

    @Test fun `navigateBack at root returns false`() { assertThat(navigationManager.navigateBack()).isFalse() }

    @Test fun `existing tab truncates stack`() { navigationManager.navigate(TestRoute.Detail); navigationManager.selectTab(TestRoute.Home); assertThat(navigationManager.backStack.value).containsExactly(TestRoute.Home) }

    @Test fun `new tab appends stack`() { navigationManager.selectTab(TestRoute.Search); assertThat(navigationManager.backStack.value).containsExactly(TestRoute.Home, TestRoute.Search).inOrder() }

    @Test fun `navigateOver replaces from target`() { navigationManager.navigate(TestRoute.Detail); navigationManager.navigate(TestRoute.Profile); navigationManager.navigateOver(TestRoute.Search, TestRoute.Detail); assertThat(navigationManager.backStack.value).containsExactly(TestRoute.Home, TestRoute.Search).inOrder() }

    @Test fun `navigateToTop resets above root`() { navigationManager.navigate(TestRoute.Detail); navigationManager.navigateToTop(TestRoute.Search); assertThat(navigationManager.backStack.value).containsExactly(TestRoute.Home, TestRoute.Search).inOrder() }

    @Test fun `bottom bar visibility uses registered items`() { assertThat(navigationManager.showBottomBar(TestRoute.Home)).isTrue(); assertThat(navigationManager.showBottomBar(TestRoute.Detail)).isFalse() }

    @Test fun `bottom items are sorted`() { assertThat(navigationManager.bottomBarItems).containsExactly(TestRoute.Home, TestRoute.Search).inOrder() }

    @Test fun `back to root resets stack`() { navigationManager.navigate(TestRoute.Detail); assertThat(navigationManager.navigateBackToRoot()).isTrue(); assertThat(navigationManager.backStack.value).containsExactly(TestRoute.Home) }

    @Test
    fun `backStack emits updates`() = runTest {
        navigationManager.navigate(TestRoute.Detail)
        assertThat(navigationManager.backStack.first()).containsExactly(TestRoute.Home, TestRoute.Detail).inOrder()
    }

    private sealed interface TestRoute : INavigationItem {
        data object Home : TestRoute, IBottomBarItem { override val route = "home"; override val icon: @androidx.compose.runtime.Composable () -> Unit = {} }
        data object Search : TestRoute, IBottomBarItem { override val route = "search"; override val icon: @androidx.compose.runtime.Composable () -> Unit = {} }
        data object Detail : TestRoute { override val route = "detail" }
        data object Profile : TestRoute { override val route = "profile" }
    }
}
