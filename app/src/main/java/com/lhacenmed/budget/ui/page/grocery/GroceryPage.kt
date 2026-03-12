package com.lhacenmed.budget.ui.page.grocery

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.lhacenmed.budget.data.local.GroceryItem
import com.lhacenmed.budget.ui.component.BudgetBottomSheet
import java.time.LocalDate

// ── Content ───────────────────────────────────────────────────────────────────

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GroceryContent(
    items: List<GroceryItem>,
    padding: PaddingValues,
    onToggle: (GroceryItem) -> Unit,
    onDelete: (Int) -> Unit,
    onEdit: (GroceryItem) -> Unit
) {
    val today = LocalDate.now().toString()
    LazyColumn(
        modifier            = Modifier.fillMaxSize().padding(padding),
        contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (items.isEmpty()) {
            item {
                Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No groceries yet. Tap + to add.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(items, key = { it.id }) { item ->
                GroceryItemCard(
                    item      = item,
                    isChecked = item.checkedDate == today,
                    onToggle  = { onToggle(item) },
                    onEdit    = { onEdit(item) },
                    onDelete  = { onDelete(item.id) }
                )
            }
        }
    }
}

// ── Item Card ─────────────────────────────────────────────────────────────────

@Composable
private fun GroceryItemCard(
    item: GroceryItem,
    isChecked: Boolean,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier          = Modifier.padding(start = 4.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = isChecked, onCheckedChange = { onToggle() })
            Text(
                text           = item.name,
                modifier       = Modifier.weight(1f).padding(start = 4.dp),
                style          = MaterialTheme.typography.bodyLarge,
                color          = if (isChecked) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurface,
                textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
            )
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

// ── Sheet (shared for Add & Edit) ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryItemSheet(
    title: String = "Add Grocery",
    initialName: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by rememberSaveable { mutableStateOf(initialName) }

    val submit = {
        if (name.isNotBlank()) {
            onConfirm(name)
            onDismiss()
        }
    }

    BudgetBottomSheet(onDismiss = onDismiss) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value         = name,
            onValueChange = { name = it },
            label         = { Text("Item name") },
            singleLine    = true,
            modifier      = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { submit() })
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick  = { submit() },
            modifier = Modifier.fillMaxWidth(),
            enabled  = name.isNotBlank()
        ) { Text(title) }
    }
}
