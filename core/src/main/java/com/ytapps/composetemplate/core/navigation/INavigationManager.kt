package com.ytapps.composetemplate.core.navigation

import kotlinx.coroutines.flow.StateFlow

interface INavigationManager {

    val backStack: StateFlow<List<INavigationItem>>
    val startDestination: INavigationItem
    val bottomBarItems: List<IBottomBarItem>

    fun showBottomBar(route: INavigationItem): Boolean
    fun selectTab(selected: IBottomBarItem)
    fun navigateBack()
    fun navigate(route: INavigationItem)
    fun navigateOver(route: INavigationItem, over: INavigationItem)
    fun navigateToTop(route: INavigationItem)
}