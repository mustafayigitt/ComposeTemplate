package com.ytapps.composetemplate.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ytapps.composetemplate.core.ui.components.AppAvatar
import com.ytapps.composetemplate.core.ui.components.AppBadge
import com.ytapps.composetemplate.core.ui.components.AppButton
import com.ytapps.composetemplate.core.ui.components.AppCard
import com.ytapps.composetemplate.core.ui.components.AppChip
import com.ytapps.composetemplate.core.ui.components.AppDivider
import com.ytapps.composetemplate.core.ui.components.AppIconButton
import com.ytapps.composetemplate.core.ui.components.AppIconButtonVariant
import com.ytapps.composetemplate.core.ui.components.AppListItem
import com.ytapps.composetemplate.core.ui.components.AppSearchField
import com.ytapps.composetemplate.core.ui.components.AppTopBar
import com.ytapps.composetemplate.core.ui.theme.AppTheme
import com.ytapps.composetemplate.core.ui.theme.ComposeTemplateTheme

@Composable
fun DesignSystemScreen() {
    Scaffold(
        topBar = {
            AppTopBar(title = "Design System")
        },
    ) { padding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(AppTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.large),
        ) {
            Section(title = "Buttons & Icons") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppButton(text = "Primary", onClick = {})
                    AppIconButton(
                        icon = Icons.Default.Add,
                        onClick = {},
                        variant = AppIconButtonVariant.FILLED,
                    )
                    AppIconButton(
                        icon = Icons.Default.Settings,
                        onClick = {},
                        variant = AppIconButtonVariant.TONAL,
                    )
                }
            }

            Section(title = "Inputs") {
                var searchText by remember { mutableStateOf("") }
                AppSearchField(value = searchText, onValueChange = { searchText = it })
            }

            Section(title = "Lists & Items") {
                AppCard {
                    AppListItem(
                        title = "List Item Title",
                        subtitle = "Detailed description goes here",
                        leadingContent = { AppAvatar(name = "User") },
                        trailingContent = { AppBadge(text = "Active") },
                    )
                    AppDivider()
                    AppListItem(
                        title = "Another Item",
                        onClick = {},
                    )
                }
            }

            Section(title = "Chips & Tags") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppChip(label = "Kotlin", selected = true, onClick = {})
                    AppChip(label = "Compose", selected = false, onClick = {})
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(AppTheme.spacing.medium))
        content()
    }
}

@ThemePreviews
@Composable
fun DesignSystemPreview() {
    ComposeTemplateTheme {
        DesignSystemScreen()
    }
}
