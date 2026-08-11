package com.ytapps.composetemplate.convention

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.net.URI
import java.util.zip.ZipFile

class ValidateSecretsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val validateSecretsTask = target.tasks.register("validateSecrets") {
            group = "setup"
            description = "Validates that secrets.properties exists with all required keys"

            doLast {
                val secretsFile = File(target.rootProject.rootDir, "secrets.properties")
                val exampleFile = File(target.rootProject.rootDir, "secrets.properties.example")
                val requiredKeys = listOf(
                    "API_KEY_DEBUG",
                    "API_KEY_RELEASE",
                    "BASE_URL_DEBUG",
                    "BASE_URL_RELEASE",
                    "XOR_MASK",
                    "EXPECTED_SIGNATURE_HASH"
                )

                val properties = loadSecretsProperties(secretsFile)
                if (!secretsFile.exists() && requiredKeys.any { properties.getSecret(it).isNullOrBlank() }) {
                    if (exampleFile.exists()) {
                        logger.error(
                            """
                            |
                            |❌ secrets.properties not found!
                            |
                            |  Copy the example file and fill in your values:
                            |    cp secrets.properties.example secrets.properties
                            |
                            |  Then edit secrets.properties with your actual keys.
                            |
                        """.trimMargin()
                        )
                    } else {
                        logger.error(
                            """
                            |
                            |❌ secrets.properties not found!
                            |
                            |  Create secrets.properties in the project root with the following keys:
                            |    API_KEY_DEBUG="your_debug_api_key"
                            |    API_KEY_RELEASE="your_release_api_key"
                            |    BASE_URL_DEBUG="https://debug.api.com/"
                            |    BASE_URL_RELEASE="https://api.com/"
                            |    XOR_MASK="your_xor_mask"
                            |    EXPECTED_SIGNATURE_HASH="your_app_signature"
                            |    CERTIFICATE_PINS="sha256/primary_pin,sha256/backup_pin"
                            |    CERTIFICATE_PINNING_ENABLED=true
                            |
                            |  For release builds, also add:
                            |    STORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD
                            |    STORE_FILE is optional and defaults to release.keystore.
                            |
                        """.trimMargin()
                        )
                    }
                    throw GradleException("secrets.properties is missing")
                }

                val releaseKeys = listOf(
                    "STORE_PASSWORD",
                    "KEY_ALIAS",
                    "KEY_PASSWORD"
                )

                val missingKeys = requiredKeys.filter { key ->
                    properties.getSecret(key).isNullOrBlank()
                }

                if (missingKeys.isNotEmpty()) {
                    logger.error(
                        """
                        |
                        |❌ secrets.properties is missing required keys:
                        |  ${missingKeys.joinToString(", ")}
                        |
                        |  Add these keys to secrets.properties.
                        |
                    """.trimMargin()
                    )
                    throw GradleException("Missing required secrets: $missingKeys")
                }

                val placeholderKeys = requiredKeys.filter { key ->
                    properties.getSecret(key)?.contains("YOUR_") == true
                }

                if (placeholderKeys.isNotEmpty()) {
                    logger.error(
                        """
                        |
                        |❌ secrets.properties still has placeholder values (YOUR_*):
                        |  ${placeholderKeys.joinToString(", ")}
                        |
                        |  Replace them with real values before building.
                        |  See secrets.properties.example for reference.
                        |
                    """.trimMargin()
                    )
                    throw GradleException("Placeholder secrets are not allowed: $placeholderKeys")
                }

                val xorMask = properties.getSecret("XOR_MASK").orEmpty()
                if (xorMask.length < MIN_XOR_MASK_LENGTH) {
                    throw GradleException(
                        "XOR_MASK must be at least $MIN_XOR_MASK_LENGTH characters to avoid weak obfuscation.",
                    )
                }
                if (!SAFE_MASK_REGEX.matches(xorMask)) {
                    throw GradleException(
                        "XOR_MASK must use only letters, numbers, '.', '_', '+', '/', '=' or '-' to keep Gradle/CMake injection safe.",
                    )
                }

                listOf("BASE_URL_DEBUG", "BASE_URL_RELEASE").forEach { key ->
                    val value = properties.getSecret(key).orEmpty()
                    validateBaseUrl(key, value)
                }

                val signatureHash = properties.getSecret("EXPECTED_SIGNATURE_HASH").orEmpty()
                validateSignatureHash(signatureHash)

                val pinningEnabled = properties.getSecret("CERTIFICATE_PINNING_ENABLED")?.toBooleanStrictOrNull() ?: false
                val certificatePins = properties.getSecret("CERTIFICATE_PINS").orEmpty()
                if (pinningEnabled) {
                    validateCertificatePins(certificatePins)
                }

                if (target.gradle.startParameter.taskNames.any { it.contains("Release", ignoreCase = true) }) {
                    val missingReleaseKeys = releaseKeys.filter { key ->
                        properties.getSecret(key).isNullOrBlank()
                    }
                    if (missingReleaseKeys.isNotEmpty()) {
                        logger.error(
                            """
                            |
                            |❌ Release build detected but missing signing config keys:
                            |  ${missingReleaseKeys.joinToString(", ")}
                            |
                            |  Release APK signing must be explicit for hardened builds.
                            |
                        """.trimMargin()
                        )
                        throw GradleException("Missing release signing secrets: $missingReleaseKeys")
                    }
                }

                logger.lifecycle("✅ secrets.properties validation complete")
            }
        }

        target.allprojects {
            pluginManager.withPlugin("com.android.application") {
                tasks.matching { it.name == "preBuild" }.configureEach {
                    dependsOn(validateSecretsTask)
                }
                tasks.matching { task ->
                    task.name in listOf("assembleRelease", "bundleRelease")
                }.configureEach {
                    finalizedBy(target.tasks.named("scanApkForSecrets"))
                }
            }
            pluginManager.withPlugin("com.android.library") {
                tasks.matching { it.name == "preBuild" }.configureEach {
                    dependsOn(validateSecretsTask)
                }
            }
        }

        target.tasks.register("scanApkForSecrets") {
            group = "verification"
            description = "Scans generated APK/AAB artifacts for raw secret values"

            doLast {
                val secretsFile = File(target.rootProject.rootDir, "secrets.properties")
                val properties = loadSecretsProperties(secretsFile)

                val needles =
                    SCANNED_SECRET_KEYS
                        .mapNotNull { key -> properties.getSecret(key)?.takeIf { it.length >= MIN_SCANNED_SECRET_LENGTH } }

                if (needles.isEmpty()) {
                    logger.lifecycle("No long secret values found to scan for.")
                    return@doLast
                }

                val artifacts =
                    target.rootProject
                        .file("app/build/outputs")
                        .walkTopDown()
                        .filter { it.isFile && it.extension in setOf("apk", "aab") }
                        .toList()

                if (artifacts.isEmpty()) {
                    logger.warn("No APK/AAB artifacts found under app/build/outputs.")
                    return@doLast
                }

                val findings =
                    artifacts.flatMap { artifact ->
                        scanZipArtifact(artifact, needles)
                    }

                if (findings.isNotEmpty()) {
                    findings.forEach { logger.error(it) }
                    throw GradleException("Raw secret values were found in generated artifacts.")
                }

                logger.lifecycle("✅ APK/AAB secret scan complete")
            }
        }

        target.tasks.register("hardeningReport") {
            group = "verification"
            description = "Prints the active client hardening configuration"

            doLast {
                val properties = java.util.Properties()
                val secretsFile = File(target.rootProject.rootDir, "secrets.properties")
                properties.putAll(loadSecretsProperties(secretsFile))

                logger.lifecycle(
                    """
                    |
                    |Client hardening report
                    |-----------------------
                    |Native secrets: ${target.findProperty("composetemplate.useNativeSecrets") ?: true}
                    |Native runtime checks: ${properties.getSecret("NATIVE_RUNTIME_CHECKS_ENABLED") ?: true}
                    |Certificate pinning: ${properties.getSecret("CERTIFICATE_PINNING_ENABLED") ?: false}
                    |Certificate pin count: ${properties.getSecret("CERTIFICATE_PINS")?.split(",")?.count { it.isNotBlank() } ?: 0}
                    |Release signature hash configured: ${!properties.getSecret("EXPECTED_SIGNATURE_HASH").isNullOrBlank()}
                    |Artifact secret scan: scanApkForSecrets
                    |
                """.trimMargin(),
                )
            }
        }
    }

    private fun java.util.Properties.getSecret(key: String): String? =
        getProperty(key)
            ?.trim()
            ?.removeSurrounding("\"")
            ?.takeUnless { it.isBlank() }

    private fun loadSecretsProperties(secretsFile: File): java.util.Properties {
        val properties = java.util.Properties()
        if (secretsFile.exists()) {
            secretsFile.inputStream().use { properties.load(it) }
        }
        System.getenv().forEach { (key, value) ->
            if (key in SECRET_KEYS && value.isNotBlank()) {
                properties.setProperty(key, value)
            }
        }
        return properties
    }

    private fun validateBaseUrl(
        key: String,
        value: String,
    ) {
        val uri =
            try {
                URI(value)
            } catch (exception: IllegalArgumentException) {
                throw GradleException("$key must be a valid absolute URL.", exception)
            }

        if (uri.scheme !in listOf("http", "https") || uri.host.isNullOrBlank()) {
            throw GradleException("$key must include http(s) scheme and host.")
        }
        if (!value.endsWith("/")) {
            throw GradleException("$key must end with '/' because Retrofit baseUrl requires it.")
        }
        if (key.endsWith("RELEASE") && uri.scheme != "https") {
            throw GradleException("$key must use HTTPS in release configuration.")
        }
    }

    private fun validateSignatureHash(signatureHash: String) {
        val normalized = signatureHash.replace(":", "").uppercase()
        if (!SHA_256_HEX_REGEX.matches(normalized)) {
            throw GradleException(
                "EXPECTED_SIGNATURE_HASH must be a SHA-256 certificate hash, with or without colons.",
            )
        }
    }

    private fun validateCertificatePins(certificatePins: String) {
        val pins = certificatePins.split(",").map { it.trim() }.filter { it.isNotBlank() }
        if (pins.size < MIN_CERTIFICATE_PIN_COUNT) {
            throw GradleException("CERTIFICATE_PINNING_ENABLED=true requires at least primary and backup pins.")
        }
        pins.forEach { pin ->
            if (!CERTIFICATE_PIN_REGEX.matches(pin)) {
                throw GradleException("Invalid certificate pin '$pin'. Expected sha256/<base64-pin>.")
            }
        }
    }

    private fun scanZipArtifact(
        artifact: File,
        needles: List<String>,
    ): List<String> {
        val findings = mutableListOf<String>()
        ZipFile(artifact).use { zip ->
            zip.entries().asSequence().forEach { entry ->
                if (entry.isDirectory) return@forEach
                val bytes = zip.getInputStream(entry).use { it.readBytes() }
                val content = bytes.toString(Charsets.ISO_8859_1)
                needles.forEach { needle ->
                    if (content.contains(needle)) {
                        findings += "❌ Raw secret found in ${artifact.name}!/${entry.name}"
                    }
                }
            }
        }
        return findings
    }

    private companion object {
        const val MIN_XOR_MASK_LENGTH = 24
        const val MIN_CERTIFICATE_PIN_COUNT = 2
        const val MIN_SCANNED_SECRET_LENGTH = 8
        val SHA_256_HEX_REGEX = Regex("^[A-F0-9]{64}$")
        val SAFE_MASK_REGEX = Regex("^[A-Za-z0-9._+/=-]+$")
        val CERTIFICATE_PIN_REGEX = Regex("^sha256/[A-Za-z0-9+/=]{44}$")
        val SCANNED_SECRET_KEYS =
            listOf(
                "API_KEY_DEBUG",
                "API_KEY_RELEASE",
                "BASE_URL_DEBUG",
                "BASE_URL_RELEASE",
            )
        val SECRET_KEYS =
            setOf(
                "API_KEY_DEBUG",
                "API_KEY_RELEASE",
                "BASE_URL_DEBUG",
                "BASE_URL_RELEASE",
                "XOR_MASK",
                "EXPECTED_SIGNATURE_HASH",
                "NATIVE_RUNTIME_CHECKS_ENABLED",
                "CERTIFICATE_PINNING_ENABLED",
                "CERTIFICATE_PINS",
                "STORE_FILE",
                "STORE_PASSWORD",
                "KEY_ALIAS",
                "KEY_PASSWORD",
            )
    }
}
