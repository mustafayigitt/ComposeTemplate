package com.lhacenmed.budget.ui.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lhacenmed.budget.ui.common.formatDate

// Reduced from the default pill/50% to feel less inflated
private val DrawerItemShape = RoundedCornerShape(12.dp)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppDrawer(
    days: List<String>,
    selectedDay: String,
    userName: String,
    userEmail: String,
    onDayClick: (String) -> Unit,
    onBudgetHistory: () -> Unit,
    onAppearance: () -> Unit,
    onSignOut: () -> Unit
) {
    ModalDrawerSheet {

        // ── User header ───────────────────────────────────────────────────────
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape  = RoundedCornerShape(50),
                color  = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector        = Icons.Default.Person,
                        contentDescription = null,
                        tint               = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier           = Modifier.size(24.dp)
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text       = userName.ifBlank { "User" },
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Text(
                    text     = userEmail,
                    style    = MaterialTheme.typography.bodySmall,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        HorizontalDivider()

        // ── Days section ──────────────────────────────────────────────────────
        Text(
            "Budget Days",
            style      = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color      = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier   = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )

        LazyColumn(modifier = Modifier.weight(1f)) {
            itemsIndexed(days) { index, day ->
                // First item  → top=8, bottom=8
                // Middle items → top=0, bottom=8
                // Last item   → top=0, bottom=0
                val topPadding    = if (index == 0) 8.dp else 0.dp
                val bottomPadding = if (index == days.lastIndex) 0.dp else 8.dp

                NavigationDrawerItem(
                    label    = { Text(formatDate(day)) },
                    selected = day == selectedDay,
                    onClick  = { onDayClick(day) },
                    shape    = DrawerItemShape,
                    modifier = Modifier.padding(
                        start  = 16.dp,
                        end    = 16.dp,
                        top    = topPadding,
                        bottom = bottomPadding
                    )
                )
            }
        }

        HorizontalDivider()

        // ── Bottom actions ────────────────────────────────────────────────────
        NavigationDrawerItem(
            label    = { Text("Budget History") },
            selected = false,
            onClick  = onBudgetHistory,
            icon     = { Icon(Icons.Default.History, contentDescription = null) },
            shape    = DrawerItemShape,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp)
        )
        NavigationDrawerItem(
            label    = { Text("Appearance") },
            selected = false,
            onClick  = onAppearance,
            icon     = { Icon(Icons.Outlined.Palette, contentDescription = null) },
            shape    = DrawerItemShape,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        NavigationDrawerItem(
            label = { Text("Sign Out", color = MaterialTheme.colorScheme.error) },
            selected = false,
            onClick  = onSignOut,
            icon     = {
                Icon(
                    Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.error
                )
            },
            shape    = DrawerItemShape,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 8.dp)
        )

        Spacer(Modifier.height(8.dp))
    }
}
