package com.lhacenmed.budget.ui.page.budget

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lhacenmed.budget.data.model.BudgetContribution
import com.lhacenmed.budget.data.model.SpendingItem
import com.lhacenmed.budget.ui.common.formatDate
import com.lhacenmed.budget.ui.common.formatTimestamp
import com.lhacenmed.budget.ui.common.format
import com.lhacenmed.budget.ui.page.home.HomeViewModel

// Unified entry for the mixed timeline
private sealed class HistoryEntry {
    abstract val timestamp: String

    data class Contribution(val data: BudgetContribution) : HistoryEntry() {
        override val timestamp get() = data.createdAt
    }
    data class Spending(val data: SpendingItem) : HistoryEntry() {
        override val timestamp get() = data.createdAt
    }
}

private enum class HistoryFilter(val label: String) {
    ALL("All"), CONTRIBUTIONS("Contributions"), SPENDINGS("Spendings")
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetHistoryPage(
    onNavigateBack: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var filter by remember { mutableStateOf(HistoryFilter.ALL) }

    // Build unified sorted list
    val allEntries = remember(state.contributions, state.allSpending) {
        (state.contributions.map { HistoryEntry.Contribution(it) } +
                state.allSpending.map { HistoryEntry.Spending(it) })
            .sortedByDescending { it.timestamp }
    }

    val filtered = remember(allEntries, filter) {
        when (filter) {
            HistoryFilter.ALL           -> allEntries
            HistoryFilter.CONTRIBUTIONS -> allEntries.filterIsInstance<HistoryEntry.Contribution>()
            HistoryFilter.SPENDINGS     -> allEntries.filterIsInstance<HistoryEntry.Spending>()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget History", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Filter segmented buttons
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                HistoryFilter.entries.forEachIndexed { index, option ->
                    SegmentedButton(
                        selected = filter == option,
                        onClick = { filter = option },
                        shape = SegmentedButtonDefaults.itemShape(index, HistoryFilter.entries.size),
                        label = { Text(option.label) }
                    )
                }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (filtered.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No entries yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered, key = { it.timestamp + (it as? HistoryEntry.Contribution)?.data?.id + (it as? HistoryEntry.Spending)?.data?.id }) { entry ->
                        when (entry) {
                            is HistoryEntry.Contribution -> ContributionEntry(entry.data)
                            is HistoryEntry.Spending     -> SpendingEntry(entry.data)
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun ContributionEntry(contribution: BudgetContribution) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    contribution.contributor,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    formatTimestamp(contribution.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
            Text(
                "+${contribution.amount.format()} dh",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun SpendingEntry(item: SpendingItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(item.name, fontWeight = FontWeight.Medium)
                    Text(
                        item.quantity,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (!item.description.isNullOrBlank()) {
                    Text(item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(
                    "${item.shopper} · ${formatDate(item.date)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Text(
                "-${item.price.format()} dh",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
