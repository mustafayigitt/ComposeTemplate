package com.ytapps.composetemplate.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.ytapps.composetemplate.core.ui.ThemePreviews
import com.ytapps.composetemplate.core.ui.theme.ComposeTemplateTheme

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(text = label) },
        isError = isError,
        visualTransformation = visualTransformation,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = MaterialTheme.shapes.medium,
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor = MaterialTheme.colorScheme.error,
            ),
    )
}

@ThemePreviews
@Composable
private fun AppTextFieldPreview() {
    ComposeTemplateTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            AppTextField(value = "", onValueChange = {}, label = "Email")
            Spacer(modifier = Modifier.height(12.dp))
            AppTextField(value = "user@example.com", onValueChange = {}, label = "Email")
            Spacer(modifier = Modifier.height(12.dp))
            AppTextField(value = "invalid", onValueChange = {}, label = "Email", isError = true)
        }
    }
}
