package com.ytapps.composetemplate.core.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AppDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    padding: Dp = 0.dp,
) {
    HorizontalDivider(
        modifier = modifier.padding(vertical = padding),
        thickness = thickness,
        color = MaterialTheme.colorScheme.outlineVariant,
    )
}
