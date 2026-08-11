package com.ytapps.composetemplate.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ytapps.composetemplate.core.ui.ThemePreviews
import com.ytapps.composetemplate.core.ui.theme.ComposeTemplateTheme

@Composable
fun AppChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = label) },
        modifier = modifier,
        enabled = enabled,
        colors = FilterChipDefaults.filterChipColors(),
    )
}

@ThemePreviews
@Composable
private fun AppChipPreview() {
    ComposeTemplateTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AppChip(label = "Unselected", selected = false, onClick = {})
            AppChip(label = "Selected", selected = true, onClick = {})
            AppChip(label = "Disabled", selected = false, onClick = {}, enabled = false)
        }
    }
}
