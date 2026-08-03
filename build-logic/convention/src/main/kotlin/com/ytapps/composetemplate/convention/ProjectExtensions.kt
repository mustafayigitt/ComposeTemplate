package com.ytapps.composetemplate.convention

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import java.util.Properties

val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

val Project.secrets: Properties
    get() {
        val properties = Properties()
        val secretsFile = rootProject.file("secrets.properties")
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

private val SECRET_KEYS =
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
        "STORE_PASSWORD",
        "KEY_ALIAS",
        "KEY_PASSWORD",
    )
