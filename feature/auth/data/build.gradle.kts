plugins {
    id("composetemplate.feature.data")
}

android {
    namespace = "com.ytapps.composetemplate.feature.auth.data"
}

dependencies {
    implementation(project(":feature:auth:domain"))
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    testImplementation(libs.okhttp)
}
