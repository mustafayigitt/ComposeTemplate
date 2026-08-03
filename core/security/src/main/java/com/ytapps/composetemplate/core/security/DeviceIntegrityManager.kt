package com.ytapps.composetemplate.core.security

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Debug
import com.ytapps.composetemplate.core.secrets.SecretManager
import java.io.File
import java.security.MessageDigest
import java.util.Locale

class DeviceIntegrityManager(
    private val context: Context,
    private val policy: SecurityPolicy,
) {
    fun evaluate(): SecurityReport {
        val findings =
            buildSet {
                if (!isExpectedPackageName()) {
                    add(SecurityFinding.PackageNameMismatch)
                }
                if (!isExpectedSignature()) {
                    add(SecurityFinding.SignatureMismatch)
                }
                if (!isExpectedInstaller()) {
                    add(SecurityFinding.UnexpectedInstaller)
                }
                if (isProbablyEmulator()) {
                    add(SecurityFinding.Emulator)
                }
                if (isDebuggerAttached()) {
                    add(SecurityFinding.DebuggerAttached)
                }
                if (hasRootSignals()) {
                    add(SecurityFinding.RootSignals)
                }
                if (hasHookingSignals()) {
                    add(SecurityFinding.HookingSignals)
                }
            }

        val action =
            when {
                findings.isEmpty() -> SecurityAction.Allow
                policy.blockOnFindings -> SecurityAction.Block
                else -> SecurityAction.Warn
            }

        return SecurityReport(action = action, findings = findings)
    }

    private fun isExpectedPackageName(): Boolean = context.packageName == policy.expectedPackageName

    private fun isExpectedSignature(): Boolean {
        val expectedHash = SecretManager.getExpectedSignatureHash().normalizeHash()
        if (expectedHash.isBlank()) return false
        return getSigningCertificateHashes().any { it == expectedHash }
    }

    private fun getSigningCertificateHashes(): List<String> {
        val packageManager = context.packageManager
        val packageInfo =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES,
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            }

        val signatures =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val signingInfo = packageInfo.signingInfo ?: return emptyList()
                if (signingInfo.hasMultipleSigners()) {
                    signingInfo.apkContentsSigners
                } else {
                    signingInfo.signingCertificateHistory
                }
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures ?: emptyArray()
            }

        return signatures.map { signature ->
            MessageDigest
                .getInstance("SHA-256")
                .digest(signature.toByteArray())
                .joinToString(separator = "") { byte -> "%02X".format(byte) }
        }
    }

    private fun isExpectedInstaller(): Boolean {
        val installer = getInstallerPackageName() ?: return true
        return installer in policy.allowedInstallers
    }

    @SuppressLint("NewApi")
    private fun getInstallerPackageName(): String? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.packageManager.getInstallSourceInfo(context.packageName).installingPackageName
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getInstallerPackageName(context.packageName)
        }

    private fun isProbablyEmulator(): Boolean {
        val fingerprint = Build.FINGERPRINT.lowercase(Locale.US)
        val model = Build.MODEL.lowercase(Locale.US)
        val manufacturer = Build.MANUFACTURER.lowercase(Locale.US)
        val brand = Build.BRAND.lowercase(Locale.US)
        val device = Build.DEVICE.lowercase(Locale.US)
        val product = Build.PRODUCT.lowercase(Locale.US)
        val hardware = Build.HARDWARE.lowercase(Locale.US)

        return fingerprint.startsWith("generic") ||
            "emulator" in model ||
            "android sdk built for" in model ||
            "genymotion" in manufacturer ||
            brand.startsWith("generic") &&
            device.startsWith("generic") ||
            product.contains("sdk") ||
            hardware in setOf("goldfish", "ranchu")
    }

    private fun isDebuggerAttached(): Boolean = Debug.isDebuggerConnected() || Debug.waitingForDebugger()

    private fun hasRootSignals(): Boolean =
        ROOT_PATHS.any { File(it).exists() } ||
            Build.TAGS?.contains("test-keys") == true

    private fun hasHookingSignals(): Boolean =
        runCatching {
            File("/proc/self/maps")
                .takeIf { it.canRead() }
                ?.useLines { lines ->
                    lines.any { line ->
                        HOOKING_MARKERS.any { marker ->
                            line.contains(marker, ignoreCase = true)
                        }
                    }
                } ?: false
        }.getOrDefault(false)

    private companion object {
        val ROOT_PATHS =
            listOf(
                "/system/app/Superuser.apk",
                "/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su",
                "/su/bin/su",
            )

        val HOOKING_MARKERS =
            listOf(
                "frida",
                "gum-js-loop",
                "xposed",
                "substrate",
                "lsposed",
            )
    }
}

private fun String.normalizeHash(): String = replace(":", "").uppercase(Locale.US)
