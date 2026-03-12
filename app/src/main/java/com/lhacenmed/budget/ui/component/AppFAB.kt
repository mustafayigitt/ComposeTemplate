package com.lhacenmed.budget.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.traversalIndex

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppFab(
    selectedTab: Int,
    expanded: Boolean,
    visible: Boolean,
    onToggle: (Boolean) -> Unit,
    onAddSpending: () -> Unit,
    onAddFunds: () -> Unit,
    onAddGrocery: () -> Unit,
) {
    when (selectedTab) {
        // Home — expressive FAB menu
        0 -> AnimatedVisibility(
            visible = visible,
            enter   = scaleIn(initialScale = 0.8f) + fadeIn(),
            exit    = scaleOut(targetScale  = 0.8f) + fadeOut(),
        ) {
            FloatingActionButtonMenu(
                expanded = expanded,
                button   = {
                    ToggleFloatingActionButton(
                        modifier        = Modifier.semantics {
                            traversalIndex     = -1f
                            stateDescription   = if (expanded) "Expanded" else "Collapsed"
                            contentDescription = "Toggle menu"
                        },
                        checked         = expanded,
                        onCheckedChange = onToggle,
                    ) {
                        val icon by remember {
                            derivedStateOf {
                                if (checkedProgress > 0.5f) Icons.Filled.Close else Icons.Filled.Add
                            }
                        }
                        Icon(icon, contentDescription = null,
                            modifier = Modifier.animateIcon({ checkedProgress }))
                    }
                }
            ) {
                FloatingActionButtonMenuItem(
                    onClick = onAddSpending,
                    icon    = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    text    = { Text("Add Spending") },
                )
                FloatingActionButtonMenuItem(
                    onClick = onAddFunds,
                    icon    = { Icon(Icons.Default.Payments, contentDescription = null) },
                    text    = { Text("Add Funds") },
                )
            }
        }

        // Groceries — simple FAB, same Scaffold positioning as home
        1 -> AnimatedVisibility(
            visible = visible,
            enter   = scaleIn(initialScale = 0.8f) + fadeIn(),
            exit    = scaleOut(targetScale  = 0.8f) + fadeOut(),
        ) {
            FloatingActionButton(onClick = onAddGrocery) {
                Icon(Icons.Default.Add, contentDescription = "Add Grocery")
            }
        }

        // Status saver — no FAB (read-only feature)
        else -> Unit
    }
}
