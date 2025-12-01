package com.ytapps.composetemplate.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey

/**
 * Created by mustafayigitt on 02/12/2025
 * mustafa.yt65@gmail.com
 */

interface INavigationItem: NavKey {
    val route: String
    @Composable
    fun ContentScreen(navigationManager: NavigationManager)

}