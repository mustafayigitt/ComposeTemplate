package com.ytapps.composetemplate.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ytapps.composetemplate.core.ui.ThemePreviews
import com.ytapps.composetemplate.core.ui.theme.AppTheme
import com.ytapps.composetemplate.core.ui.theme.ComposeTemplateTheme

@Composable
fun AppBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(4.dp))
                .background(containerColor)
                .padding(horizontal = AppTheme.spacing.small, vertical = 2.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
        )
    }
}

@ThemePreviews
@Composable
private fun AppBadgePreview() {
    ComposeTemplateTheme {
        Row(modifier = Modifier.padding(16.dp)) {
            AppBadge(text = "1")
            Spacer(modifier = Modifier.width(8.dp))
            AppBadge(text = "99+")
            Spacer(modifier = Modifier.width(8.dp))
            AppBadge(text = "New")
        }
    }
}
