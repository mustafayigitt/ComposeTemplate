package com.ytapps.composetemplate.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.ytapps.composetemplate.core.ui.ThemePreviews
import com.ytapps.composetemplate.core.ui.theme.ComposeTemplateTheme

@Composable
fun AppIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AppIconButtonVariant = AppIconButtonVariant.PLAIN,
    contentDescription: String? = null,
    enabled: Boolean = true,
) {
    when (variant) {
        AppIconButtonVariant.PLAIN -> {
            IconButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                )
            }
        }
        AppIconButtonVariant.TONAL -> {
            FilledTonalIconButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                )
            }
        }
        AppIconButtonVariant.FILLED -> {
            FilledIconButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                )
            }
        }
    }
}

@ThemePreviews
@Composable
private fun AppIconButtonPreview() {
    ComposeTemplateTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AppIconButton(
                icon = Icons.Default.Favorite,
                onClick = {},
                variant = AppIconButtonVariant.PLAIN,
            )
            AppIconButton(
                icon = Icons.Default.Favorite,
                onClick = {},
                variant = AppIconButtonVariant.TONAL,
            )
            AppIconButton(
                icon = Icons.Default.Favorite,
                onClick = {},
                variant = AppIconButtonVariant.FILLED,
            )
        }
    }
}

enum class AppIconButtonVariant {
    PLAIN,
    TONAL,
    FILLED,
}
