package com.lhacenmed.budget.ui.page

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.lhacenmed.budget.ui.common.Route
import com.lhacenmed.budget.ui.common.animatedComposable
import com.lhacenmed.budget.ui.page.appearance.AppearancePage
import com.lhacenmed.budget.ui.page.appearance.DarkThemePage
import com.lhacenmed.budget.ui.page.budget.BudgetHistoryPage
import com.lhacenmed.budget.ui.page.home.HomePage

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppEntry() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Route.HOME) {
        animatedComposable(Route.HOME) {
            HomePage(
                onNavigateToBudgetHistory = { navController.navigate(Route.BUDGET_HISTORY) },
                onNavigateToAppearance    = { navController.navigate(Route.APPEARANCE) },
            )
        }
        animatedComposable(Route.BUDGET_HISTORY) {
            BudgetHistoryPage(onNavigateBack = { navController.popBackStack() })
        }
        animatedComposable(Route.APPEARANCE) {
            AppearancePage(
                onNavigateBack        = { navController.popBackStack() },
                onNavigateToDarkTheme = { navController.navigate(Route.DARK_THEME) },
            )
        }
        animatedComposable(Route.DARK_THEME) {
            DarkThemePage(onNavigateBack = { navController.popBackStack() })
        }
    }
}
