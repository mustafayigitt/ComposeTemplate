package com.lhacenmed.budget.ui.page.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lhacenmed.budget.data.model.BudgetContribution
import com.lhacenmed.budget.data.model.SpendingItem
import com.lhacenmed.budget.ui.common.format
import com.lhacenmed.budget.ui.common.formatDate
import com.lhacenmed.budget.ui.page.auth.AuthViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    onNavigateToBudgetHistory: () -> Unit,
    onNavigateToAppearance: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showAddSpending by remember { mutableStateOf(false) }
    var showAddFunds    by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DaysDrawer(
                days = state.days,
                selectedDay = state.selectedDay,
                onDayClick = { day ->
                    viewModel.selectDay(day)
                    scope.launch { drawerState.close() }
                },
                onBudgetHistory = {
                    scope.launch { drawerState.close() }
                    onNavigateToBudgetHistory()
                },
                onAppearance = {                   // ← add this
                    scope.launch { drawerState.close() }
                    onNavigateToAppearance()
                },
                onSignOut = { authViewModel.signOut() }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(formatDate(state.selectedDay), fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showAddSpending = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add item")
                        }
                    }
                )
            }
        ) { padding ->
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(Modifier.padding(padding).fillMaxSize()) {

                    // Offline banner
                    if (!state.isOnline || state.pendingCount > 0) {
                        Surface(color = MaterialTheme.colorScheme.tertiaryContainer) {
                            Text(
                                text = if (!state.isOnline)
                                    "You're offline — items will sync when reconnected"
                                else
                                    "${state.pendingCount} item(s) syncing…",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }

                    BudgetSummaryRow(
                        daySpent = state.daySpent,
                        remaining = state.remaining,
                        onAddBudget = { showAddFunds = true }
                    )
                    DayContent(
                        modifier = Modifier.weight(1f),
                        items = state.items,
                        onDelete = { viewModel.deleteItem(it) }
                    )
                }
            }
        }
    }

    if (showAddSpending) {
        AddSpendingSheet(
            shopperName = state.currentUserName,
            onDismiss = { showAddSpending = false },
            onConfirm = { name, quantity, price, description ->
                viewModel.addItem(name, quantity, price, description)
            }
        )
    }

    if (showAddFunds) {
        AddFundsSheet(
            onDismiss = { showAddFunds = false },
            onConfirm = { amount -> viewModel.addContribution(amount) }
        )
    }
}

// ── Budget Summary Row ────────────────────────────────────────────────────────

@Composable
private fun BudgetSummaryRow(
    daySpent: Float,
    remaining: Float,
    onAddBudget: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Today's Spending",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer)
                Text("${daySpent.format()} dh",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer)
            }
        }
        Card(
            onClick = onAddBudget,
            modifier = Modifier.weight(1f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Remaining",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text("${remaining.format()} dh",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (remaining < 0) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onPrimaryContainer)
                Text("Tap to add funds",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
            }
        }
    }
}

// ── Days Drawer ───────────────────────────────────────────────────────────────

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DaysDrawer(
    days: List<String>,
    selectedDay: String,
    onDayClick: (String) -> Unit,
    onBudgetHistory: () -> Unit,
    onAppearance: () -> Unit,
    onSignOut: () -> Unit
) {
    ModalDrawerSheet {
        Spacer(Modifier.height(16.dp))
        Text("Budget Days",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontWeight = FontWeight.Bold)
        HorizontalDivider()
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(days) { day ->
                NavigationDrawerItem(
                    label = { Text(formatDate(day)) },
                    selected = day == selectedDay,
                    onClick = { onDayClick(day) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
        HorizontalDivider()
        NavigationDrawerItem(
            label = { Text("Budget History") },
            selected = false,
            onClick = onBudgetHistory,
            icon = { Icon(Icons.Default.History, contentDescription = null) },
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp, end = 16.dp)
        )
        NavigationDrawerItem(                      // ← add this item
            label = { Text("Appearance") },
            selected = false,
            onClick = onAppearance,
            icon = { Icon(Icons.Outlined.Palette, contentDescription = null) },
            modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 4.dp, end = 16.dp)
        )
        NavigationDrawerItem(
            label = { Text("Sign Out", color = MaterialTheme.colorScheme.error) },
            selected = false,
            onClick = onSignOut,
            icon = {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null,
                    tint = MaterialTheme.colorScheme.error)
            },
            modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 8.dp, end = 16.dp)
        )
        Spacer(Modifier.height(8.dp))
    }
}

// ── Day Content ───────────────────────────────────────────────────────────────

@Composable
private fun DayContent(
    modifier: Modifier,
    items: List<SpendingItem>,
    onDelete: (String) -> Unit
) {
    val dayTotal = items.sumOf { it.price.toDouble() }

    Column(modifier = modifier) {
        Text(
            "Spendings",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (items.isEmpty()) {
                item {
                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No spendings yet. Tap + to add.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(items, key = { it.id }) { item ->
                    SpendingItemCard(item = item, onDelete = { onDelete(item.id) })
                }
                item {
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Day total", fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium)
                        Text("${dayTotal.format()} dh", fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun SpendingItemCard(item: SpendingItem, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(item.name, fontWeight = FontWeight.Medium)
                    Text(item.quantity,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (!item.description.isNullOrBlank()) {
                    Text(item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("by ${item.shopper}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline)
            }
            Text("${item.price.format()} dh", fontWeight = FontWeight.SemiBold)
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
