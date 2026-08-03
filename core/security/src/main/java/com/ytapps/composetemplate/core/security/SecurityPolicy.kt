package com.ytapps.composetemplate.core.security

data class SecurityPolicy(
    val expectedPackageName: String,
    val allowedInstallers: Set<String> = defaultAllowedInstallers,
    val blockOnFindings: Boolean,
) {
    companion object {
        val defaultAllowedInstallers =
            setOf(
                "com.android.vending",
                "com.google.android.packageinstaller",
                "com.android.packageinstaller",
            )
    }
}
