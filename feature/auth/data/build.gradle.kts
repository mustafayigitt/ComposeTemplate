plugins {
    id("composetemplate.feature.data")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ytapps.composetemplate.feature.auth.data"
}

dependencies {
    implementation(project(":feature:auth:domain"))
    implementation(libs.retrofit)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.okhttp)
}
