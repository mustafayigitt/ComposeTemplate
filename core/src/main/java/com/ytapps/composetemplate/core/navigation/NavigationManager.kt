package com.ytapps.composetemplate.core.navigation


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationManager @Inject constructor(
    override val startDestination: INavigationItem,
    private val bottomBarItemsRaw: Map<String, @JvmSuppressWildcards IBottomBarItem>
) : INavigationManager {

    private val _backStack = MutableStateFlow(listOf(startDestination))
    override val backStack = _backStack.asStateFlow()

    override val bottomBarItems: List<IBottomBarItem> =
        bottomBarItemsRaw.entries.sortedBy { it.key }.map { it.value }

    override fun showBottomBar(route: INavigationItem): Boolean {
        return route in bottomBarItems
    }

    override fun selectTab(selected: IBottomBarItem) {
        _backStack.update { currentStack ->
            val existingIndex = currentStack.indexOfFirst { it.route == selected.route }

            if (existingIndex != -1) {
                currentStack.take(existingIndex + 1)
            } else {
                currentStack + selected
            }
        }
    }

    override fun navigateBack() {
        _backStack.update { currentStack ->
            if (currentStack.size > 1) {
                currentStack.dropLast(1)
            } else {
                emptyList()
            }
        }
    }

    override fun navigate(route: INavigationItem) {
        _backStack.update { stack ->
            stack + route
        }
    }

    override fun navigateOver(route: INavigationItem, over: INavigationItem) {
        _backStack.update { stack ->
            val existingIndex = stack.indexOfFirst { it.route == over.route }
            if (existingIndex != -1) {
                stack.dropLast(stack.size - existingIndex) + route
            } else {
                stack + route
            }
        }
    }

    override fun navigateToTop(route: INavigationItem) {
        navigateOver(route = route, over = startDestination)
    }
}