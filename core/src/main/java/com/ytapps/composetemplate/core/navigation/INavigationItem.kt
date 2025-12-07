package com.ytapps.composetemplate.core.navigation

import androidx.navigation3.runtime.NavKey

interface INavigationItem: NavKey {
    val route: String
}