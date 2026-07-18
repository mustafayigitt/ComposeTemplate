package com.ytapps.composetemplate.core.ui.components

import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

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

enum class AppIconButtonVariant {
    PLAIN,
    TONAL,
    FILLED,
}
