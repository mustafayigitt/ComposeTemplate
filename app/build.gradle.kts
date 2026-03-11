import java.util.Properties

plugins {
    id("composetemplate.create.new.app")
    id("composetemplate.android.application")
    id("composetemplate.android.application.compose")
    id("composetemplate.android.hilt")
    id("composetemplate.test")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.lhacenmed.budget"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.lhacenmed.budget"
        targetSdk = 36
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

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
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

    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.auth)
    implementation(libs.ktor.client.android)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.androidx.datastore.preferences)
}
