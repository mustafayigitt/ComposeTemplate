package com.lhacenmed.budget.ui.page.auth

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.lhacenmed.budget.ui.common.Route
import com.lhacenmed.budget.ui.common.animatedComposable

@Composable
fun AuthEntry() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Route.LOGIN) {
        animatedComposable(Route.LOGIN) {
            LoginPage(onNavigateToRegister = { navController.navigate(Route.REGISTER) })
        }
        animatedComposable(Route.REGISTER) {
            RegisterPage(onNavigateBack = { navController.popBackStack() })
        }
    }
}
