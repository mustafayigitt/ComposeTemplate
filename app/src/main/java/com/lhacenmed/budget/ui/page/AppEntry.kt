package com.lhacenmed.budget.ui.page

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.lhacenmed.budget.data.local.GroceryItem
import com.lhacenmed.budget.ui.common.animatedComposable
import com.lhacenmed.budget.ui.common.formatDate
import com.lhacenmed.budget.ui.common.Route
import com.lhacenmed.budget.ui.page.appearance.AppearancePage
import com.lhacenmed.budget.ui.page.appearance.DarkThemePage
import com.lhacenmed.budget.ui.page.budget.BudgetHistoryPage
import com.lhacenmed.budget.ui.page.grocery.GroceryContent
import com.lhacenmed.budget.ui.page.grocery.GroceryItemSheet
import com.lhacenmed.budget.ui.page.grocery.GroceryViewModel
import com.lhacenmed.budget.ui.page.home.AddFundsSheet
import com.lhacenmed.budget.ui.page.home.AddSpendingSheet
import com.lhacenmed.budget.ui.page.home.AppDrawer
import com.lhacenmed.budget.ui.page.home.HomeFab
import com.lhacenmed.budget.ui.page.home.HomeContent
import com.lhacenmed.budget.ui.page.home.HomeViewModel
import com.lhacenmed.budget.ui.page.auth.AuthViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppEntry() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Route.HOME) {
        animatedComposable(Route.HOME) {
            MainScreen(
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

// ── Main screen ───────────────────────────────────────────────────────────────

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    onNavigateToBudgetHistory: () -> Unit,
    onNavigateToAppearance: () -> Unit,
    homeViewModel:    HomeViewModel    = hiltViewModel(),
    groceryViewModel: GroceryViewModel = hiltViewModel(),
    authViewModel:    AuthViewModel    = hiltViewModel(),
) {
    val homeState    by homeViewModel.state.collectAsStateWithLifecycle()
    val groceryItems by groceryViewModel.items.collectAsStateWithLifecycle()

    var selectedTab     by rememberSaveable { mutableIntStateOf(0) }
    var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var showAddSpending by remember { mutableStateOf(false) }
    var showAddFunds    by remember { mutableStateOf(false) }
    var showAddGrocery  by remember { mutableStateOf(false) }
    var editingGrocery  by remember { mutableStateOf<GroceryItem?>(null) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val listState   = rememberLazyListState()
    val scope       = rememberCoroutineScope()

    val fabVisible by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 || fabMenuExpanded }
    }

    BackHandler(enabled = fabMenuExpanded) { fabMenuExpanded = false }

    ModalNavigationDrawer(
        drawerState   = drawerState,
        drawerContent = {
            AppDrawer(
                days            = homeState.days,
                selectedDay     = homeState.selectedDay,
                onDayClick      = { day ->
                    homeViewModel.selectDay(day)
                    scope.launch { drawerState.close() }
                },
                onBudgetHistory = {
                    scope.launch { drawerState.close() }
                    onNavigateToBudgetHistory()
                },
                onAppearance    = {
                    scope.launch { drawerState.close() }
                    onNavigateToAppearance()
                },
                onSignOut       = { authViewModel.signOut() }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        val title = if (selectedTab == 0) formatDate(homeState.selectedDay) else "Groceries"
                        Text(title, fontWeight = FontWeight.SemiBold)
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick  = { selectedTab = 0; fabMenuExpanded = false },
                        icon     = {
                            Icon(
                                if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick  = { selectedTab = 1; fabMenuExpanded = false },
                        icon     = {
                            Icon(
                                if (selectedTab == 1) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                                contentDescription = "Groceries"
                            )
                        },
                        label = { Text("Groceries") }
                    )
                }
            },
            floatingActionButton = {
                when (selectedTab) {
                    0 -> HomeFab(
                        expanded      = fabMenuExpanded,
                        visible       = fabVisible,
                        onToggle      = { fabMenuExpanded = it },
                        onAddSpending = { fabMenuExpanded = false; showAddSpending = true },
                        onAddFunds    = { fabMenuExpanded = false; showAddFunds = true }
                    )
                    1 -> FloatingActionButton(onClick = { showAddGrocery = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Grocery")
                    }
                }
            }
        ) { padding ->
            when (selectedTab) {
                0 -> HomeContent(
                    state     = homeState,
                    padding   = padding,
                    listState = listState,
                    onDelete  = homeViewModel::deleteItem,
                    onAddFunds = { showAddFunds = true }
                )
                1 -> GroceryContent(
                    items    = groceryItems,
                    padding  = padding,
                    onToggle = groceryViewModel::toggleItem,
                    onDelete = groceryViewModel::deleteItem,
                    onEdit   = { editingGrocery = it }
                )
            }
        }
    }

    // ── Sheets ────────────────────────────────────────────────────────────────

    if (showAddSpending) {
        AddSpendingSheet(
            shopperName = homeState.currentUserName,
            onDismiss   = { showAddSpending = false },
            onConfirm   = { name, quantity, price, description ->
                homeViewModel.addItem(name, quantity, price, description)
            }
        )
    }
    if (showAddFunds) {
        AddFundsSheet(
            onDismiss = { showAddFunds = false },
            onConfirm = homeViewModel::addContribution
        )
    }
    if (showAddGrocery) {
        GroceryItemSheet(
            onDismiss = { showAddGrocery = false },
            onConfirm = groceryViewModel::addItem
        )
    }
    editingGrocery?.let { item ->
        GroceryItemSheet(
            title       = "Edit Grocery",
            initialName = item.name,
            onDismiss   = { editingGrocery = null },
            onConfirm   = { name -> groceryViewModel.updateItem(item.id, name) }
        )
    }
}
