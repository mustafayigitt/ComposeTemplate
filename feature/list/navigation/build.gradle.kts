plugins {
    id("composetemplate.feature.navigation")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ytapps.composetemplate.feature.list.navigation"
}

dependencies {
    implementation(libs.kotlinx.serialization.core)
}
