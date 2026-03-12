@file:Suppress("UnstableApiUsage")

import com.android.build.api.variant.FilterConfiguration
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
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
    val file = projectDir.resolve("../local.properties")
    if (file.exists()) load(file.inputStream())
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
                keyAlias      = keystoreProperties["keyAlias"].toString()
                keyPassword   = keystoreProperties["keyPassword"].toString()
                storeFile     = file(keystoreProperties["storeFile"]!!)
                storePassword = keystoreProperties["storePassword"].toString()
            }
        }
    }

    defaultConfig {
        applicationId             = "com.lhacenmed.budget"
        minSdk                    = 23
        targetSdk                 = 36
        versionCode               = currentVersion.run { versionMajor * 10000 + versionMinor * 100 + versionPatch }
        versionName               = currentVersion.toVersionName()
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

    // TODO: fix this alert: w: [ksp] Schema export directory was not provided to the annotation processor so Room cannot export the schema. You can either provide `room.schemaLocation` annotation processor argument by applying the Room Gradle plugin (id 'androidx.room') OR set exportSchema to false.

    buildTypes {
        debug {
            isMinifyEnabled   = false
            isShrinkResources = false
            applicationIdSuffix  = ".debug"
            versionNameSuffix    = "-debug"
            resValue("string", "app_name", "Budget Debug")
            buildConfigField("String", "BASE_URL", "\"${localProperties.getProperty("BASE_URL_DEBUG") ?: ""}\"")
            if (keystorePropertiesFile.exists()) signingConfig = signingConfigs.getByName("debug")
        }
        release {
            isMinifyEnabled   = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", "BASE_URL", "\"${localProperties.getProperty("BASE_URL") ?: ""}\"")
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

kotlin {
    jvmToolchain(17)
}

// ── Dependencies ──────────────────────────────────────────────────────────────

dependencies {
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
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.android.material)

    // Media
    implementation(libs.coil.compose)
    implementation(libs.androidx.documentfile)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.ui)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

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