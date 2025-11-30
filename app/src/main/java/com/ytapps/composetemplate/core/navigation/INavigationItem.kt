package com.ytapps.composetemplate.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey

/**
 * Created by mustafayigitt on 25/08/2023
 * mustafa.yt65@gmail.com
 */

interface INavigationItem: NavKey {
    val route: String
    //fun navEntry(navigationManager: NavigationManager): NavEntry<INavigationItem>
    @Composable
    fun ContentScreen(navigationManager: NavigationManager)

}