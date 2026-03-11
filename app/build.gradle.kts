@file:Suppress("UnstableApiUsage")

import com.android.build.api.variant.FilterConfiguration
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("composetemplate.create.new.app")
    id("composetemplate.android.application")
    id("composetemplate.android.application.compose")
    id("composetemplate.android.hilt")
    id("composetemplate.test")
    alias(libs.plugins.kotlin.serialization)
}

// ── Versioning ────────────────────────────────────────────────────────────────

sealed class Version(
    open val versionMajor: Int,
    val versionMinor: Int,
    val versionPatch: Int,
    val versionBuild: Int = 0,
) {
    abstract fun toVersionName(): String

    class Alpha(versionMajor: Int, versionMinor: Int, versionPatch: Int, versionBuild: Int) :
        Version(versionMajor, versionMinor, versionPatch, versionBuild) {
        override fun toVersionName() = "$versionMajor.$versionMinor.$versionPatch-alpha.$versionBuild"
    }

    class Beta(versionMajor: Int, versionMinor: Int, versionPatch: Int, versionBuild: Int) :
        Version(versionMajor, versionMinor, versionPatch, versionBuild) {
        override fun toVersionName() = "$versionMajor.$versionMinor.$versionPatch-beta.$versionBuild"
    }

    class Stable(versionMajor: Int, versionMinor: Int, versionPatch: Int) :
        Version(versionMajor, versionMinor, versionPatch) {
        override fun toVersionName() = "$versionMajor.$versionMinor.$versionPatch"
    }

    class ReleaseCandidate(versionMajor: Int, versionMinor: Int, versionPatch: Int, versionBuild: Int) :
        Version(versionMajor, versionMinor, versionPatch, versionBuild) {
        override fun toVersionName() = "$versionMajor.$versionMinor.$versionPatch-rc.$versionBuild"
    }
}

val currentVersion: Version = Version.Stable(
    versionMajor = 1,
    versionMinor = 0,
    versionPatch = 0,
)

// ── Build config ──────────────────────────────────────────────────────────────

val localProperties = Properties().apply {
    load(projectDir.resolve("../local.properties").inputStream())
}

val keystorePropertiesFile: File = rootProject.file("keystore.properties")
val splitApks = !project.hasProperty("noSplits")
val abiFilterList = (properties["ABI_FILTERS"] as? String)?.split(';') ?: listOf("arm64-v8a")

android {
    namespace  = "com.lhacenmed.budget"
    compileSdk = 36

    if (keystorePropertiesFile.exists()) {
        val keystoreProperties = Properties()
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
        signingConfigs {
            getByName("debug") {
                keyAlias    = keystoreProperties["keyAlias"].toString()
                keyPassword = keystoreProperties["keyPassword"].toString()
                storeFile   = file(keystoreProperties["storeFile"]!!)
                storePassword = keystoreProperties["storePassword"].toString()
            }
        }
    }

    defaultConfig {
        applicationId          = "com.lhacenmed.budget"
        targetSdk              = 36
        versionCode            = currentVersion.run { versionMajor * 10000 + versionMinor * 100 + versionPatch }
        versionName            = currentVersion.toVersionName()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true

        if (splitApks) {
            splits {
                abi {
                    isEnable = true
                    reset()
                    include("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
                    isUniversalApk = true
                }
            }
        } else {
            ndk { abiFilters.addAll(abiFilterList) }
        }
    }

    val abiCodes = mapOf("armeabi-v7a" to 1, "arm64-v8a" to 2, "x86" to 3, "x86_64" to 4)
    androidComponents {
        onVariants { variant ->
            variant.outputs.forEach { output ->
                val name = if (splitApks) {
                    output.filters.find { it.filterType == FilterConfiguration.FilterType.ABI }?.identifier
                } else {
                    abiFilterList.firstOrNull()
                }
                abiCodes[name]?.let { code ->
                    output.versionCode.set(code + (output.versionCode.get() ?: 0))
                }
            }
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled   = false
            isShrinkResources = false
            applicationIdSuffix  = ".debug"
            versionNameSuffix    = "-debug"
            resValue("string", "app_name", "Budget Debug")
            buildConfigField("String", "BASE_URL", "\"${localProperties.getProperty("BASE_URL_DEBUG")}\"")
            if (keystorePropertiesFile.exists()) signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled   = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "BASE_URL", "\"${localProperties.getProperty("BASE_URL")}\"")
            if (keystorePropertiesFile.exists()) signingConfig = signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        compose     = true
        buildConfig = true
    }

    lint {
        disable.addAll(listOf("MissingTranslation", "ExtraTranslation", "MissingQuantity"))
    }

    applicationVariants.all {
        outputs.all {
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                "Budget-${defaultConfig.versionName}-${name}.apk"
        }
    }

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
        jniLibs.useLegacyPackaging = true
    }
}

kotlin {
    jvmToolchain(17)
}

// ── Dependencies ──────────────────────────────────────────────────────────────

dependencies {
    // Feature modules
    implementation(project(":core"))
    implementation(project(":contract"))
    implementation(project(":feature:auth:data"))
    implementation(project(":feature:auth:navigation"))
    implementation(project(":feature:auth:presentation"))
    implementation(project(":feature:detail:navigation"))
    implementation(project(":feature:detail:presentation"))
    implementation(project(":feature:home:navigation"))
    implementation(project(":feature:home:presentation"))
    implementation(project(":feature:list:navigation"))
    implementation(project(":feature:list:presentation"))
    implementation(project(":feature:profile:navigation"))
    implementation(project(":feature:profile:presentation"))
    implementation(project(":feature:search:navigation"))
    implementation(project(":feature:search:presentation"))
    implementation(project(":feature:splash:navigation"))
    implementation(project(":feature:splash:presentation"))

    // Supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.auth)
    implementation(libs.ktor.client.android)

    // AndroidX
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)

    // Compose
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.material3)
    implementation(libs.android.material)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Serialization
    implementation(libs.kotlinx.serialization.core)
}

// ── Room schema KSP arg provider ──────────────────────────────────────────────

class RoomSchemaArgProvider(
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val schemaDir: File,
) : CommandLineArgumentProvider {
    override fun asArguments() = listOf("room.schemaLocation=${schemaDir.path}")
}