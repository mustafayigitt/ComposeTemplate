import java.util.Properties
import kotlin.apply

plugins {
    id("composetemplate.android.application")
    id("composetemplate.android.application.compose")
    id("composetemplate.android.hilt")
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "com.ytapps.composetemplate"

    defaultConfig {
        applicationId = "com.ytapps.composetemplate"
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    val localProperties = Properties().apply {
        load(projectDir.resolve("../local.properties").inputStream())
    }
    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            "\"${localProperties.getProperty("BASE_URL_DEBUG")}\""
            buildConfigField(
                "String",
                "BASE_URL",
                "\"${localProperties.getProperty("BASE_URL_DEBUG")}\""
            )
        }
        
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField(
                "String",
                "BASE_URL",
                "\"${localProperties.getProperty("BASE_URL")}\""
            )
        }
    }
    
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":contract"))
    implementation(project(":feature:auth:data"))
    implementation(project(":feature:auth:presentation"))
    implementation(project(":feature:detail:presentation"))
    implementation(project(":feature:home:presentation"))
    implementation(project(":feature:list:presentation"))
    implementation(project(":feature:profile:presentation"))
    implementation(project(":feature:search:presentation"))
    implementation(project(":feature:splash:presentation"))

    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.material3.adaptive.navigation3)
    implementation(libs.kotlinx.serialization.core)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}