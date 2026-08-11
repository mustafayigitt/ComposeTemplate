package com.ytapps.composetemplate.core.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ytapps.composetemplate.core.permission.components.PermissionRationaleDialog
import com.ytapps.composetemplate.core.permission.components.PermissionSettingsDialog

@Composable
fun PermissionWrapper(
    permission: String,
    rationaleTitle: String,
    rationaleMessage: String,
    permanentlyDeniedTitle: String,
    permanentlyDeniedMessage: String,
    content: @Composable (PermissionStatus) -> Unit,
) {
    val handler = rememberPermissionHandler(permission)
    var showRationale by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    content(handler.status)

    if (showRationale) {
        PermissionRationaleDialog(
            title = rationaleTitle,
            message = rationaleMessage,
            onConfirm = {
                showRationale = false
                handler.requestPermission()
            },
            onDismiss = { showRationale = false },
        )
    }

    if (showSettings) {
        PermissionSettingsDialog(
            title = permanentlyDeniedTitle,
            message = permanentlyDeniedMessage,
            onDismiss = { showSettings = false },
        )
    }

    // Logic to trigger dialogs can be handled by the caller or here via a launched effect if we want auto-trigger.
    // For now, we provide a way to request through the handler passed to content.
}

@Composable
fun PermissionRequired(
    permission: String,
    rationaleTitle: String,
    rationaleMessage: String,
    permanentlyDeniedTitle: String,
    permanentlyDeniedMessage: String,
    onPermissionGranted: @Composable () -> Unit,
    onPermissionDenied: @Composable (status: PermissionStatus, requestPermission: () -> Unit) -> Unit,
) {
    val handler = rememberPermissionHandler(permission)
    var showRationale by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    if (handler.status is PermissionStatus.Granted) {
        onPermissionGranted()
    } else {
        onPermissionDenied(handler.status) {
            when (handler.status) {
                is PermissionStatus.RationaleRequired -> showRationale = true
                is PermissionStatus.PermanentlyDenied -> showSettings = true
                else -> handler.requestPermission()
            }
        }
    }

    if (showRationale) {
        PermissionRationaleDialog(
            title = rationaleTitle,
            message = rationaleMessage,
            onConfirm = {
                showRationale = false
                handler.requestPermission()
            },
            onDismiss = { showRationale = false },
        )
    }

    if (showSettings) {
        PermissionSettingsDialog(
            title = permanentlyDeniedTitle,
            message = permanentlyDeniedMessage,
            onDismiss = { showSettings = false },
        )
    }
}
