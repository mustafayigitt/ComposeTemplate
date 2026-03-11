package com.lhacenmed.budget.ui

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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ExitToApp
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
import com.lhacenmed.budget.ui.auth.AuthViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showAddSheet by remember { mutableStateOf(false) }
    var showBudgetSheet by remember { mutableStateOf(false) }

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
                        IconButton(onClick = { showAddSheet = true }) {
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
                    BudgetCard(
                        totalBudget = state.totalBudget,
                        totalSpent = state.totalSpent,
                        remaining = state.remaining,
                        onClick = { showBudgetSheet = true }
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

    if (showAddSheet) {
        AddItemSheet(
            shopperName = state.currentUserName,
            onDismiss = { showAddSheet = false },
            onConfirm = { name, quantity, price, description ->
                viewModel.addItem(name, quantity, price, description)
                showAddSheet = false
            }
        )
    }

    if (showBudgetSheet) {
        BudgetSheet(
            contributions = state.contributions,
            onDismiss = { showBudgetSheet = false },
            onAddContribution = { amount ->
                viewModel.addContribution(amount)
                showBudgetSheet = false
            }
        )
    }
}

// ── Budget Card ───────────────────────────────────────────────────────────────

@Composable
private fun BudgetCard(
    totalBudget: Float,
    totalSpent: Float,
    remaining: Float,
    onClick: () -> Unit
) {
    val isNegative = remaining < 0
    val remainingColor = if (isNegative) MaterialTheme.colorScheme.error
    else MaterialTheme.colorScheme.primary

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Budget", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "Tap to manage",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            HorizontalDivider()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                BudgetStat(label = "Added", value = "+${totalBudget.format()} dh",
                    color = MaterialTheme.colorScheme.secondary)
                BudgetStat(label = "Spent", value = "-${totalSpent.format()} dh",
                    color = MaterialTheme.colorScheme.error)
                BudgetStat(label = "Remaining", value = "${remaining.format()} dh",
                    color = remainingColor, bold = true)
            }
            if (totalBudget > 0) {
                val progress = (totalSpent / totalBudget).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = if (isNegative) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun BudgetStat(label: String, value: String, color: androidx.compose.ui.graphics.Color, bold: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = color,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
    }
}

// ── Budget Sheet ──────────────────────────────────────────────────────────────

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BudgetSheet(
    contributions: List<BudgetContribution>,
    onDismiss: () -> Unit,
    onAddContribution: (Float) -> Unit
) {
    var amount by remember { mutableStateOf("") }
    val isValid = amount.toFloatOrNull()?.let { it > 0 } == true

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Budget", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = amount, onValueChange = { amount = it },
                    label = { Text("Add amount (dh)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                Button(
                    onClick = { onAddContribution(amount.toFloat()) },
                    enabled = isValid,
                    modifier = Modifier.height(56.dp)
                ) { Text("Add") }
            }

            if (contributions.isNotEmpty()) {
                Text("History", style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(contributions, key = { it.id }) { ContributionRow(it) }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ContributionRow(contribution: BudgetContribution) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(contribution.contributor, style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium)
            Text(formatTimestamp(contribution.createdAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text("+${contribution.amount.format()} dh",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.secondary)
    }
}

// ── Days Drawer ───────────────────────────────────────────────────────────────

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DaysDrawer(
    days: List<String>,
    selectedDay: String,
    onDayClick: (String) -> Unit,
    onSignOut: () -> Unit
) {
    ModalDrawerSheet {
        Spacer(Modifier.height(16.dp))
        Text("Budget Days", style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontWeight = FontWeight.Bold)
        HorizontalDivider()
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(days) { day ->
                NavigationDrawerItem(
                    label = { Text(formatDate(day)) },
                    selected = day == selectedDay,
                    onClick = { onDayClick(day) },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
        HorizontalDivider()
        NavigationDrawerItem(
            label = { Text("Sign Out", color = MaterialTheme.colorScheme.error) },
            selected = false,
            onClick = onSignOut,
            icon = {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null,
                    tint = MaterialTheme.colorScheme.error)
            },
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        )
    }
}

// ── Day Content ───────────────────────────────────────────────────────────────

@Composable
private fun DayContent(modifier: Modifier, items: List<SpendingItem>, onDelete: (String) -> Unit) {
    val dayTotal = items.sumOf { it.price.toDouble() }

    LazyColumn(
        modifier = modifier,
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

// ── Add Item Sheet ────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddItemSheet(
    shopperName: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Float, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val isValid = name.isNotBlank() && quantity.isNotBlank() && price.toFloatOrNull() != null

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Add Spending", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = shopperName, onValueChange = {},
                label = { Text("Shopper") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false, singleLine = true
            )
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Item name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = quantity, onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    placeholder = { Text("1kg, 2 bottles…", style = MaterialTheme.typography.bodySmall) },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = price, onValueChange = { price = it },
                    label = { Text("Price (dh)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Description (optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Button(
                onClick = { onConfirm(name, quantity, price.toFloat(), description) },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Add") }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

@RequiresApi(Build.VERSION_CODES.O)
private fun formatDate(date: String): String = runCatching {
    val d = LocalDate.parse(date)
    val today = LocalDate.now()
    when (d) {
        today -> "Today — ${d.format(DateTimeFormatter.ofPattern("MMM d"))}"
        today.minusDays(1) -> "Yesterday — ${d.format(DateTimeFormatter.ofPattern("MMM d"))}"
        else -> d.format(DateTimeFormatter.ofPattern("EEE, MMM d"))
    }
}.getOrDefault(date)

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTimestamp(raw: String): String = runCatching {
    val zdt = ZonedDateTime.parse(raw)
    zdt.format(DateTimeFormatter.ofPattern("MMM d, HH:mm"))
}.getOrDefault(raw)

private fun Float.format() = if (this == kotlin.math.floor(this)) this.toInt().toString() else "%.2f".format(this)
private fun Double.format() = if (this == kotlin.math.floor(this)) this.toInt().toString() else "%.2f".format(this)
