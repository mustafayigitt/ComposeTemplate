package com.ytapps.composetemplate.core.googleplay.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun AppUpdateDialog(
    isForceUpdate: Boolean,
    onUpdateClick: () -> Unit,
    onDismiss: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = { if (!isForceUpdate) onDismiss() },
        title = { Text(text = "New Update Available") },
        text = {
            Text(
                text =
                    if (isForceUpdate) {
                        "A critical update is required to continue using the app. Please update to the latest version."
                    } else {
                        "A new version of the app is available. Would you like to update now?"
                    },
            )
        },
        confirmButton = {
            TextButton(onClick = onUpdateClick) {
                Text(text = "Update Now")
            }
        },
        dismissButton = {
            if (!isForceUpdate) {
                TextButton(onClick = onDismiss) {
                    Text(text = "Later")
                }
            }
        },
    )
}
