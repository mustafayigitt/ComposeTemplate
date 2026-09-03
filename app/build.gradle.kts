import com.ytapps.composetemplate.convention.secrets
import java.util.Properties

plugins {
    id("composetemplate.create.new.app")
    id("composetemplate.android.application")
    id("composetemplate.perf")
    id("composetemplate.android.application.compose")
    id("composetemplate.android.hilt")
    id("composetemplate.test")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ytapps.composetemplate"

    defaultConfig {
        applicationId = "com.ytapps.composetemplate"
        versionCode =
            libs.versions.versionCode
                .get()
                .toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    val localProperties =
        Properties().apply {
            val localPropertiesFile = rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                localPropertiesFile.inputStream().use { load(it) }
            }
        }

    fun signingValue(key: String): String? =
        secrets
            .getProperty(key)
            ?.replace("\"", "")
            ?: localProperties.getProperty(key)

    signingConfigs {
        create("release") {
            storeFile = file(signingValue("STORE_FILE") ?: "release.keystore")
            keyAlias = signingValue("KEY_ALIAS")
            keyPassword = signingValue("KEY_PASSWORD")
            storePassword = signingValue("STORE_PASSWORD")
        }
    }

    buildTypes {
        debug {
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }

        // Kept here deliberately. This build type references only app-local files, so it stays
        // valid when the benchmark and baselineprofile modules are deleted; those modules select
        // it through matchingFallbacks rather than the other way around.
        create("benchmark") {
            initWith(getByName("release"))
            matchingFallbacks += listOf("release")
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "benchmark-rules.pro",
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // Every core and feature module found on disk is wired in automatically, so deleting a
    // module's folder removes it from the build without an edit here, and scaffolding a new
    // feature needs no edit either. Intermediate path projects such as :feature:auth own no
    // build file and are skipped.
    rootProject.subprojects
        .filter { it.buildFile.isFile }
        .map { it.path }
        .filter { it.startsWith(":core:") || it.startsWith(":feature:") }
        .sorted()
        .forEach { implementation(project(it)) }

    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.timber)

    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.material3.adaptive.navigation3)
    implementation(libs.kotlinx.serialization.core)

    // Baseline profile wiring, including the profileinstaller runtime dependency, is contributed
    // by composetemplate.perf and only when :baselineprofile exists.
}
