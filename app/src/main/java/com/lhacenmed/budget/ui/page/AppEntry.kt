package com.lhacenmed.budget.ui.page

import android.os.Build
import android.util.TypedValue
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.AppCompatImageButton
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
// import androidx.compose.material.BottomNavigation
// import androidx.compose.material.BottomNavigationItem
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.lhacenmed.budget.data.local.GroceryItem
import com.lhacenmed.budget.ui.common.HapticFeedback.longPressHapticFeedback
import com.lhacenmed.budget.ui.common.Route
import com.lhacenmed.budget.ui.common.animatedComposable
import com.lhacenmed.budget.ui.common.formatDate
import com.lhacenmed.budget.ui.component.AppDrawer
import com.lhacenmed.budget.ui.component.AppFab
import com.lhacenmed.budget.ui.page.appearance.AppearancePage
import com.lhacenmed.budget.ui.page.appearance.DarkThemePage
import com.lhacenmed.budget.ui.page.auth.AuthViewModel
import com.lhacenmed.budget.ui.page.budget.BudgetHistoryPage
import com.lhacenmed.budget.ui.page.grocery.GroceryContent
import com.lhacenmed.budget.ui.page.grocery.GroceryItemSheet
import com.lhacenmed.budget.ui.page.grocery.GroceryViewModel
import com.lhacenmed.budget.ui.page.home.AddFundsSheet
import com.lhacenmed.budget.ui.page.home.AddSpendingSheet
import com.lhacenmed.budget.ui.page.home.HomeContent
import com.lhacenmed.budget.ui.page.home.HomeViewModel
import com.lhacenmed.budget.ui.page.status.StatusContent
import com.lhacenmed.budget.ui.page.status.StatusViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.Q)
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

@RequiresApi(Build.VERSION_CODES.Q)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    onNavigateToBudgetHistory: () -> Unit,
    onNavigateToAppearance: () -> Unit,
    homeViewModel:    HomeViewModel    = hiltViewModel(),
    groceryViewModel: GroceryViewModel = hiltViewModel(),
    statusViewModel:  StatusViewModel  = hiltViewModel(),
    authViewModel:    AuthViewModel    = hiltViewModel(),
) {
    val homeState    by homeViewModel.state.collectAsStateWithLifecycle()
    val groceryItems by groceryViewModel.items.collectAsStateWithLifecycle()
    val statusState  by statusViewModel.state.collectAsStateWithLifecycle()

    var selectedTab     by rememberSaveable { mutableIntStateOf(0) }
    var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var showAddSpending by remember { mutableStateOf(false) }
    var showAddFunds    by remember { mutableStateOf(false) }
    var showAddGrocery  by remember { mutableStateOf(false) }
    var editingGrocery  by remember { mutableStateOf<GroceryItem?>(null) }

    val drawerState   = rememberDrawerState(DrawerValue.Closed)
    val listState     = rememberLazyListState()
    val snackbarState = remember { SnackbarHostState() }
    val scope         = rememberCoroutineScope()
    val view          = LocalView.current

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
                onBudgetHistory = { scope.launch { drawerState.close() }; onNavigateToBudgetHistory() },
                onAppearance    = { scope.launch { drawerState.close() }; onNavigateToAppearance() },
                onSignOut       = { authViewModel.signOut() }
            )
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarState) },
            topBar = {
                TopAppBar(
                    title = {
                        val title = when (selectedTab) {
                            0    -> formatDate(homeState.selectedDay)
                            1    -> "Groceries"
                            else -> "Status Saver"
                        }
                        Text(title, fontWeight = FontWeight.SemiBold)
                    },
                    navigationIcon = {
                        AndroidView(
                            modifier = Modifier.size(48.dp),
                            factory  = { ctx ->
                                AppCompatImageButton(ctx).apply {
                                    setImageDrawable(
                                        DrawerArrowDrawable(ctx).apply { progress = 0f }
                                    )
                                    val tv = TypedValue()
                                    ctx.theme.resolveAttribute(
                                        android.R.attr.selectableItemBackgroundBorderless,
                                        tv, true
                                    )
                                    setBackgroundResource(tv.resourceId)
                                    ViewCompat.setTooltipText(this, "Open navigation drawer")
                                    setOnClickListener { scope.launch { drawerState.open() } }
                                    contentDescription = "Menu"
                                }
                            }
                        )
                        // Return it to this if want the ripple effect
                        // IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        //     Icon(Icons.Default.Menu, contentDescription = "Menu")
                        // }

                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick  = { view.longPressHapticFeedback(); selectedTab = 0; fabMenuExpanded = false },
                        icon     = { Icon(if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home, "Home") },
                        label    = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick  = { view.longPressHapticFeedback(); selectedTab = 1; fabMenuExpanded = false },
                        icon     = { Icon(if (selectedTab == 1) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart, "Groceries") },
                        label    = { Text("Groceries") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick  = { view.longPressHapticFeedback(); selectedTab = 2; fabMenuExpanded = false },
                        icon     = { Icon(if (selectedTab == 2) Icons.Filled.CloudDownload else Icons.Outlined.CloudDownload, "Status") },
                        label    = { Text("Status") }
                    )
                }
            },
            floatingActionButton = {
                AppFab(
                    selectedTab   = selectedTab,
                    expanded      = fabMenuExpanded,
                    visible       = fabVisible,
                    onToggle      = { fabMenuExpanded = it },
                    onAddSpending = { fabMenuExpanded = false; showAddSpending = true },
                    onAddFunds    = { fabMenuExpanded = false; showAddFunds = true },
                    onAddGrocery  = { showAddGrocery = true }
                )
            }
        ) { padding ->
            when (selectedTab) {
                0 -> HomeContent(
                    state      = homeState,
                    padding    = padding,
                    listState  = listState,
                    onDelete   = homeViewModel::deleteItem,
                    onAddFunds = { showAddFunds = true }
                )
                1 -> GroceryContent(
                    items    = groceryItems,
                    padding  = padding,
                    onToggle = groceryViewModel::toggleItem,
                    onDelete = groceryViewModel::deleteItem,
                    onEdit   = { editingGrocery = it }
                )
                2 -> StatusContent(
                    state               = statusState,
                    padding             = padding,
                    onPermissionGranted = statusViewModel::onPermissionGranted,
                    onSave              = statusViewModel::saveStatus,
                    onItemClick         = statusViewModel::openPreview,
                    onClosePreview      = statusViewModel::closePreview,
                    onShowSnackbar      = { msg ->
                        statusViewModel.clearMessage()
                        scope.launch { snackbarState.showSnackbar(msg) }
                    }
                )
            }
        }
    }

    // ── Sheets ────────────────────────────────────────────────────────────────

    if (showAddSpending) {
        AddSpendingSheet(
            shopperName = homeState.currentUserName,
            onDismiss   = { showAddSpending = false },
            onConfirm   = { name, qty, price, desc -> homeViewModel.addItem(name, qty, price, desc) }
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
