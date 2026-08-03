package com.ytapps.composetemplate

import android.app.Application
import com.ytapps.composetemplate.core.secrets.SecretManager
import com.ytapps.composetemplate.core.security.DeviceIntegrityManager
import com.ytapps.composetemplate.core.security.SecurityPolicy
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SecretManager.initialize(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        enforceClientHardeningPolicy()
    }

    private fun enforceClientHardeningPolicy() {
        val report =
            DeviceIntegrityManager(
                context = this,
                policy =
                    SecurityPolicy(
                        expectedPackageName = BuildConfig.APPLICATION_ID,
                        blockOnFindings = !BuildConfig.DEBUG && SecretManager.isNativeRuntimeChecksEnabled(),
                    ),
            ).evaluate()

        if (report.findings.isNotEmpty()) {
            Timber.w("Client hardening findings: %s", report.findings.joinToString())
        }
        check(!report.isBlocked) {
            "Client hardening policy blocked app startup: ${report.findings.joinToString()}"
        }
    }
}
