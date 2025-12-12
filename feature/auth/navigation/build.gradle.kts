plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ytapps.composetemplate.feature.auth.navigation"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":contract"))
    implementation(libs.kotlinx.serialization.core)
}

