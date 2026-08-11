package com.ytapps.composetemplate.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ytapps.composetemplate.core.ui.ThemePreviews
import com.ytapps.composetemplate.core.ui.theme.ComposeTemplateTheme

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier =
            modifier
                .fillMaxWidth()
                .height(56.dp),
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@ThemePreviews
@Composable
private fun AppButtonPreview() {
    ComposeTemplateTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            AppButton(text = "Sign In", onClick = {})
            Spacer(modifier = Modifier.height(12.dp))
            AppButton(text = "Disabled", onClick = {}, enabled = false)
            Spacer(modifier = Modifier.height(12.dp))
            AppButton(text = "A very long button text for testing", onClick = {})
        }
    }
}
