package com.ytapps.composetemplate.core.permission

sealed class PermissionStatus {
    object Granted : PermissionStatus()

    object Denied : PermissionStatus()

    object RationaleRequired : PermissionStatus()

    object PermanentlyDenied : PermissionStatus()
}
