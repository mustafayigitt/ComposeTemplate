package com.ytapps.composetemplate.core.navigation

import com.ytapps.composetemplate.presentation.home.Home
import com.ytapps.composetemplate.presentation.profile.Profile
import com.ytapps.composetemplate.presentation.search.Search
import com.ytapps.composetemplate.presentation.splash.Splash
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class NavigationManager @Inject constructor() {
    // can be set programmatically
    val startDestination: INavigationItem = Splash
    val bottomBarItems: List<IBottomBarItem> = listOf(
        Home,
        Search,
        Profile,
    )

    private val _backStack = MutableStateFlow(listOf(startDestination))
    val backStack = _backStack.asStateFlow()

    fun showBottomBar(route: INavigationItem): Boolean {
        return bottomBarItems.any { it == route }
    }

    fun selectTab(selected: IBottomBarItem) {
        if (_backStack.value.last() != selected) {
            _backStack.update { stack ->
                stack + selected
            }
            return
        }
        val existingIndex = _backStack.value.indexOfFirst { it.route == selected.route }
        if (existingIndex != -1) {
            _backStack.update { stack ->
                stack.dropLast(stack.size - existingIndex - 1)
            }
        }
    }

    fun navigateBack() {
        if (_backStack.value.size > 1) {
            _backStack.update {
                it.dropLast(1)
            }
        } else {
            _backStack.update { emptyList() }
        }
    }

    fun navigate(route: INavigationItem) {
        _backStack.update { stack ->
            stack + route
        }
    }

    fun navigateOver(route: INavigationItem, over: INavigationItem) {
        _backStack.update { stack ->
            val existingIndex = stack.indexOfFirst { it.route == over.route }
            if (existingIndex != -1) {
                stack.dropLast(stack.size - existingIndex) + route
            } else {
                stack + route
            }
        }
    }

    fun navigateToTop(route: INavigationItem) {
        navigateOver(route = route, over = startDestination)
    }

}