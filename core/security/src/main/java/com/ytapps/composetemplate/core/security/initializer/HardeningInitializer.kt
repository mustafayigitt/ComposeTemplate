package com.ytapps.composetemplate.core.security.initializer

import android.app.Application
import android.content.pm.ApplicationInfo
import com.ytapps.composetemplate.core.common.initializer.AppInitializer
import com.ytapps.composetemplate.core.secrets.SecretManager
import com.ytapps.composetemplate.core.security.DeviceIntegrityManager
import com.ytapps.composetemplate.core.security.SecurityPolicy
import timber.log.Timber
import javax.inject.Inject

/**
 * Evaluates the client hardening policy at startup and blocks launch when the device or
 * the installed package fails the policy.
 *
 * Debuggability is read from the running application instead of the app module's
 * `BuildConfig`, so this initializer stays independent of `:app`.
 */
internal class HardeningInitializer
    @Inject
    constructor() : AppInitializer {
        override val order: Int = AppInitializer.ORDER_SECURITY

        override fun init(app: Application) {
            val isDebuggable = (app.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
            val report =
                DeviceIntegrityManager(
                    context = app,
                    policy =
                        SecurityPolicy(
                            expectedPackageName = app.packageName,
                            blockOnFindings = !isDebuggable && SecretManager.isNativeRuntimeChecksEnabled(),
                        ),
                ).evaluate()

            if (report.findings.isNotEmpty()) {
                Timber.w("[core:security] Client hardening findings: %s", report.findings.joinToString())
            }
            check(!report.isBlocked) {
                "[core:security] Client hardening policy blocked app startup: ${report.findings.joinToString()}"
            }
        }
    }
