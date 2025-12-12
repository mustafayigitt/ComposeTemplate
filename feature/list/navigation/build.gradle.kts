plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "com.ytapps.composetemplate.feature.list.navigation"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":contract"))
    implementation(libs.kotlinx.serialization.core)
}
