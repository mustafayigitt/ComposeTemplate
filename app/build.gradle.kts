import com.ytapps.composetemplate.convention.secrets
import java.util.Properties

plugins {
    id("composetemplate.create.new.app")
    id("composetemplate.android.application")
    alias(libs.plugins.baselineprofile)
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
        rootProject.file("local.properties").inputStream().use { stream ->
            Properties().apply { load(stream) }
        }

    signingConfigs {
        create("release") {
            storeFile = file(localProperties.getProperty("STORE_FILE") ?: error("STORE_FILE not found in local.properties"))
            keyAlias = secrets.getProperty("KEY_ALIAS")?.replace("\"", "")
            keyPassword = secrets.getProperty("KEY_PASSWORD")?.replace("\"", "")
            storePassword = secrets.getProperty("STORE_PASSWORD")?.replace("\"", "")
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
    implementation(project(":core:common"))
    implementation(project(":core:secrets"))
    implementation(project(":core:data"))
    implementation(project(":core:network"))
    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))
    implementation(project(":core:analytics"))
    implementation(project(":core:config"))
    implementation(project(":core:permission"))
    implementation(project(":core:google-play"))
    implementation(project(":core:database"))
    implementation(project(":feature:auth:data"))
    implementation(project(":feature:auth:domain"))
    implementation(project(":feature:auth:navigation"))
    implementation(project(":feature:auth:presentation"))
    implementation(project(":feature:detail:data"))
    implementation(project(":feature:detail:domain"))
    implementation(project(":feature:detail:navigation"))
    implementation(project(":feature:detail:presentation"))
    implementation(project(":feature:home:data"))
    implementation(project(":feature:home:domain"))
    implementation(project(":feature:home:navigation"))
    implementation(project(":feature:home:presentation"))
    implementation(project(":feature:list:data"))
    implementation(project(":feature:list:domain"))
    implementation(project(":feature:list:navigation"))
    implementation(project(":feature:list:presentation"))
    implementation(project(":feature:onboarding:data"))
    implementation(project(":feature:onboarding:domain"))
    implementation(project(":feature:onboarding:navigation"))
    implementation(project(":feature:onboarding:presentation"))
    implementation(project(":feature:profile:data"))
    implementation(project(":feature:profile:domain"))
    implementation(project(":feature:profile:navigation"))
    implementation(project(":feature:profile:presentation"))
    implementation(project(":feature:search:data"))
    implementation(project(":feature:search:domain"))
    implementation(project(":feature:search:navigation"))
    implementation(project(":feature:search:presentation"))
    implementation(project(":feature:splash:data"))
    implementation(project(":feature:splash:domain"))
    implementation(project(":feature:splash:navigation"))
    implementation(project(":feature:splash:presentation"))

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

    implementation(libs.androidx.profileinstaller)

    baselineProfile(project(":baselineprofile"))
}
