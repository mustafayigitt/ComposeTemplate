package com.ytapps.composetemplate.convention

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class ValidateSecretsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.register("validateSecrets") {
            group = "setup"
            description = "Validates that secrets.properties exists with all required keys"

            doLast {
                val secretsFile = File(target.rootProject.rootDir, "secrets.properties")
                val exampleFile = File(target.rootProject.rootDir, "secrets.properties.example")

                if (!secretsFile.exists()) {
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
                            |
                            |  For release builds, also add:
                            |    STORE_PASSWORD, KEY_ALIAS, KEY_PASSWORD
                            |
                        """.trimMargin()
                        )
                    }
                    throw GradleException("secrets.properties is missing")
                }

                val requiredKeys = listOf(
                    "API_KEY_DEBUG",
                    "API_KEY_RELEASE",
                    "BASE_URL_DEBUG",
                    "BASE_URL_RELEASE",
                    "XOR_MASK",
                    "EXPECTED_SIGNATURE_HASH"
                )

                val releaseKeys = listOf(
                    "STORE_PASSWORD",
                    "KEY_ALIAS",
                    "KEY_PASSWORD"
                )

                val properties = java.util.Properties()
                secretsFile.inputStream().use { properties.load(it) }

                val missingKeys = requiredKeys.filter { key ->
                    properties.getProperty(key).isNullOrBlank() || properties.getProperty(key) == "\"\""
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

                val hasDefaultPlaceholders = requiredKeys.any { key ->
                    properties.getProperty(key)?.contains("YOUR_") == true
                }

                if (hasDefaultPlaceholders) {
                    logger.warn(
                        """
                        |
                        |⚠️ secrets.properties still has placeholder values (YOUR_*).
                        |  Replace them with real values before building.
                        |  See secrets.properties.example for reference.
                        |
                    """.trimMargin()
                    )
                }

                if (target.gradle.startParameter.taskNames.any { it.contains("Release", ignoreCase = true) }) {
                    val missingReleaseKeys = releaseKeys.filter { key ->
                        properties.getProperty(key).isNullOrBlank() || properties.getProperty(key) == "\"\""
                    }
                    if (missingReleaseKeys.isNotEmpty()) {
                        logger.warn(
                            """
                            |
                            |⚠️ Release build detected but missing signing config keys:
                            |  ${missingReleaseKeys.joinToString(", ")}
                            |
                            |  Release APK signing may fail.
                            |
                        """.trimMargin()
                        )
                    }
                }

                logger.lifecycle("✅ secrets.properties validation complete")
            }
        }
    }
}
