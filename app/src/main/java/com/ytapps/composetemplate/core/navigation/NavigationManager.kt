package com.ytapps.composetemplate.core.navigation

import com.ytapps.composetemplate.ui.home.Home
import com.ytapps.composetemplate.ui.profile.Profile
import com.ytapps.composetemplate.ui.search.Search
import com.ytapps.composetemplate.ui.splash.Splash

object NavigationManager {
    // can be set programmatically
    var startDestination: INavigationItem = Splash
    var bottomBarItems: List<IBottomBarItem> = listOf(
        Home,
        Search,
        Profile,
    )

    fun isBottomBarItem(route: String): Boolean {
        return bottomBarItems.any { it.route == route }
    }
}