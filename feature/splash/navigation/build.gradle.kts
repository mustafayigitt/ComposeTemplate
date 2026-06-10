plugins {
    id("composetemplate.feature.navigation")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ytapps.composetemplate.feature.splash.navigation"
}

dependencies {
    implementation(libs.kotlinx.serialization.core)
}
