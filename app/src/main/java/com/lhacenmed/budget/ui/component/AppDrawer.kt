package com.lhacenmed.budget.ui.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lhacenmed.budget.ui.common.formatDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppDrawer(
    days: List<String>,
    selectedDay: String,
    onDayClick: (String) -> Unit,
    onBudgetHistory: () -> Unit,
    onAppearance: () -> Unit,
    onSignOut: () -> Unit
) {
    ModalDrawerSheet {
        Spacer(Modifier.height(16.dp))
        Text(
            "Budget Days",
            style      = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier   = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        HorizontalDivider()
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(days) { day ->
                NavigationDrawerItem(
                    label    = { Text(formatDate(day)) },
                    selected = day == selectedDay,
                    onClick  = { onDayClick(day) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
        HorizontalDivider()
        NavigationDrawerItem(
            label    = { Text("Budget History") },
            selected = false,
            onClick  = onBudgetHistory,
            icon     = { Icon(Icons.Default.History, contentDescription = null) },
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp, end = 16.dp)
        )
        NavigationDrawerItem(
            label    = { Text("Appearance") },
            selected = false,
            onClick  = onAppearance,
            icon     = { Icon(Icons.Outlined.Palette, contentDescription = null) },
            modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 4.dp, end = 16.dp)
        )
        NavigationDrawerItem(
            label    = { Text("Sign Out", color = MaterialTheme.colorScheme.error) },
            selected = false,
            onClick  = onSignOut,
            icon     = {
                Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.error
                )
            },
            modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 8.dp, end = 16.dp)
        )
        Spacer(Modifier.height(8.dp))
    }
}
