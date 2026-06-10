package com.ytapps.composetemplate.core.navigation

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import org.junit.Before
import org.junit.Test

internal class NavigationManagerTest {

    private lateinit var navigationManager: NavigationManager

    @Before
    fun setUp() {
        navigationManager = NavigationManager(
            startDestination = TestRoute.Home,
            bottomBarItemsRaw = mapOf(
                "1" to TestRoute.Home,
                "2" to TestRoute.Search,
            ),
        )
    }

    @Test
    fun `given initial state then back stack contains only start destination`() {
        val stack = navigationManager.backStack.value
        assertThat(stack).containsExactly(TestRoute.Home)
    }

    @Test
    fun `given single item when navigate then route is added to stack`() {
        navigationManager.navigate(TestRoute.Detail)

        val stack = navigationManager.backStack.value
        assertThat(stack).containsExactly(TestRoute.Home, TestRoute.Detail)
    }

    @Test
    fun `given multiple items when navigateBack then last item is removed`() {
        navigationManager.navigate(TestRoute.Detail)
        navigationManager.navigate(TestRoute.Profile)

        navigationManager.navigateBack()

        val stack = navigationManager.backStack.value
        assertThat(stack).containsExactly(TestRoute.Home, TestRoute.Detail)
    }

    @Test
    fun `given single item when navigateBack then stack stays with same item`() {
        navigationManager.navigateBack()

        val stack = navigationManager.backStack.value
        assertThat(stack).containsExactly(TestRoute.Home)
    }

    @Test
    fun `given tab selected when already in stack then truncates to that tab`() {
        navigationManager.navigate(TestRoute.Detail)
        navigationManager.selectTab(TestRoute.Home)

        val stack = navigationManager.backStack.value
        assertThat(stack).containsExactly(TestRoute.Home)
    }

    @Test
    fun `given tab selected when not in stack then appends to stack`() {
        navigationManager.selectTab(TestRoute.Search)

        val stack = navigationManager.backStack.value
        assertThat(stack).containsExactly(TestRoute.Home, TestRoute.Search)
    }

    @Test
    fun `given route when navigateOver existing route then replaces from that point`() {
        navigationManager.navigate(TestRoute.Detail)
        navigationManager.navigate(TestRoute.Profile)

        navigationManager.navigateOver(route = TestRoute.Search, over = TestRoute.Detail)

        val stack = navigationManager.backStack.value
        assertThat(stack).containsExactly(TestRoute.Home, TestRoute.Search)
    }

    @Test
    fun `given route when navigateOver non-existing route then appends`() {
        navigationManager.navigateOver(route = TestRoute.Search, over = TestRoute.Detail)

        val stack = navigationManager.backStack.value
        assertThat(stack).containsExactly(TestRoute.Home, TestRoute.Search)
    }

    @Test
    fun `given route when navigateToTop then replaces from start destination`() {
        navigationManager.navigate(TestRoute.Detail)
        navigationManager.navigate(TestRoute.Profile)

        navigationManager.navigateToTop(TestRoute.Search)

        val stack = navigationManager.backStack.value
        assertThat(stack).containsExactly(TestRoute.Home, TestRoute.Search)
    }

    @Test
    fun `given bottom bar route then showBottomBar returns true`() {
        val result = navigationManager.showBottomBar(TestRoute.Home)

        assertThat(result).isTrue()
    }

    @Test
    fun `given non-bottom bar route then showBottomBar returns false`() {
        val result = navigationManager.showBottomBar(TestRoute.Detail)

        assertThat(result).isFalse()
    }

    @Test
    fun `given bottomBarItems then returns sorted by key`() {
        val items = navigationManager.bottomBarItems

        assertThat(items).hasSize(2)
        assertThat(items[0]).isEqualTo(TestRoute.Home)
        assertThat(items[1]).isEqualTo(TestRoute.Search)
    }

    @Test
    fun `given backStack is StateFlow then emits updates`() = kotlinx.coroutines.test.runTest {
        navigationManager.navigate(TestRoute.Detail)

        val stack = navigationManager.backStack.first()
        assertThat(stack).containsExactly(TestRoute.Home, TestRoute.Detail)
    }

    private sealed interface TestRoute : INavigationItem {
        data object Home : TestRoute, IBottomBarItem {
            override val route: String get() = "home"
            override val icon: @androidx.compose.runtime.Composable () -> Unit = {}
        }

        data object Search : TestRoute, IBottomBarItem {
            override val route: String get() = "search"
            override val icon: @androidx.compose.runtime.Composable () -> Unit = {}
        }

        data object Detail : TestRoute {
            override val route: String get() = "detail"
        }

        data object Profile : TestRoute {
            override val route: String get() = "profile"
        }
    }
}
