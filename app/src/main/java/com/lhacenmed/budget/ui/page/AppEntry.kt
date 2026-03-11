package com.lhacenmed.budget.ui.page

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.lhacenmed.budget.ui.common.Route
import com.lhacenmed.budget.ui.common.animatedComposable
import com.lhacenmed.budget.ui.page.budget.BudgetHistoryPage
import com.lhacenmed.budget.ui.page.home.HomePage

@Composable
fun AppEntry() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Route.HOME) {
        animatedComposable(Route.HOME) {
            HomePage(onNavigateToBudgetHistory = { navController.navigate(Route.BUDGET_HISTORY) })
        }
        animatedComposable(Route.BUDGET_HISTORY) {
            BudgetHistoryPage(onNavigateBack = { navController.popBackStack() })
        }
    }
}
