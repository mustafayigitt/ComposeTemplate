package com.ytapps.composetemplate.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ytapps.composetemplate.core.ui.ThemePreviews
import com.ytapps.composetemplate.core.ui.theme.ComposeTemplateTheme

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

@ThemePreviews
@Composable
private fun AppDividerPreview() {
    ComposeTemplateTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Text("Default Divider", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            AppDivider()
            Spacer(modifier = Modifier.height(24.dp))
            Text("Thick Divider", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(8.dp))
            AppDivider(thickness = 4.dp)
        }
    }
}
