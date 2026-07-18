package com.ytapps.composetemplate.core.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@Composable
fun rememberPermissionHandler(permission: String): PermissionHandler {
    val context = LocalContext.current
    var status by remember { mutableStateOf(getPermissionStatus(context, permission)) }

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            status =
                if (isGranted) {
                    PermissionStatus.Granted
                } else {
                    if (shouldShowRationale(context, permission)) {
                        PermissionStatus.RationaleRequired
                    } else {
                        PermissionStatus.PermanentlyDenied
                    }
                }
        }

    return remember(status) {
        PermissionHandler(
            status = status,
            requestPermission = { launcher.launch(permission) },
            updateStatus = { status = getPermissionStatus(context, permission) },
        )
    }
}

class PermissionHandler(
    val status: PermissionStatus,
    val requestPermission: () -> Unit,
    val updateStatus: () -> Unit,
)

private fun getPermissionStatus(
    context: Context,
    permission: String,
): PermissionStatus =
    when {
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
            PermissionStatus.Granted
        }
        shouldShowRationale(context, permission) -> {
            PermissionStatus.RationaleRequired
        }
        else -> {
            // This could be initial state or permanently denied.
            // We'll treat it as Denied initially until we try to request.
            PermissionStatus.Denied
        }
    }

private fun shouldShowRationale(
    context: Context,
    permission: String,
): Boolean {
    val activity = context as? Activity ?: return false
    return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
}
