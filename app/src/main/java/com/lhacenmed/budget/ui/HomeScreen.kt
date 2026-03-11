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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lhacenmed.budget.data.model.SpendingItem
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showAddSheet by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DaysDrawer(
                days = state.days,
                selectedDay = state.selectedDay,
                onDayClick = { day ->
                    viewModel.selectDay(day)
                    scope.launch { drawerState.close() }
                }
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
                DayContent(
                    modifier = Modifier.padding(padding),
                    items = state.items,
                    onDelete = { viewModel.deleteItem(it) }
                )
            }
        }
    }

    if (showAddSheet) {
        AddItemSheet(
            onDismiss = { showAddSheet = false },
            onConfirm = { shopper, name, qty, price ->
                viewModel.addItem(shopper, name, qty, price)
                showAddSheet = false
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DaysDrawer(days: List<String>, selectedDay: String, onDayClick: (String) -> Unit) {
    ModalDrawerSheet {
        Spacer(Modifier.height(16.dp))
        Text(
            "Budget Days",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontWeight = FontWeight.Bold
        )
        HorizontalDivider()
        LazyColumn {
            items(days) { day ->
                NavigationDrawerItem(
                    label = { Text(formatDate(day)) },
                    selected = day == selectedDay,
                    onClick = { onDayClick(day) },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun DayContent(modifier: Modifier, items: List<SpendingItem>, onDelete: (String) -> Unit) {
    val total = items.sumOf { it.total.toDouble() }
    val shopper = items.firstOrNull()?.shopper

    Column(modifier.fillMaxSize()) {
        if (shopper != null) {
            Text(
                "Shopper: $shopper",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (items.isEmpty()) {
                item {
                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No spendings yet. Tap + to add.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(items, key = { it.id }) { item ->
                    SpendingItemCard(item = item, onDelete = { onDelete(item.id) })
                }
            }
        }

        if (items.isNotEmpty()) {
            HorizontalDivider()
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("${total.format()} dh", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun SpendingItemCard(item: SpendingItem, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Medium)
                Text(
                    "${item.quantity.format()} × ${item.price.format()} dh",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text("${item.total.format()} dh", fontWeight = FontWeight.SemiBold)
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddItemSheet(onDismiss: () -> Unit, onConfirm: (String, String, Float, Float) -> Unit) {
    var shopper by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var price by remember { mutableStateOf("") }

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
                value = shopper, onValueChange = { shopper = it },
                label = { Text("Shopper (you / friend name)") },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Item name") },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = quantity, onValueChange = { quantity = it },
                    label = { Text("Qty") }, modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true
                )
                OutlinedTextField(
                    value = price, onValueChange = { price = it },
                    label = { Text("Price (dh)") }, modifier = Modifier.weight(2f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true
                )
            }

            val isValid = shopper.isNotBlank() && name.isNotBlank() &&
                    quantity.toFloatOrNull() != null && price.toFloatOrNull() != null

            Button(
                onClick = { onConfirm(shopper, name, quantity.toFloat(), price.toFloat()) },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Add") }
        }
    }
}

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

private fun Float.format() = if (this == kotlin.math.floor(this)) this.toInt().toString() else "%.2f".format(this)
private fun Double.format() = if (this == kotlin.math.floor(this)) this.toInt().toString() else "%.2f".format(this)
