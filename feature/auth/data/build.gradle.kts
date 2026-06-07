plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
    id("composetemplate.test")
}

android {
    namespace = "com.ytapps.composetemplate.feature.auth.data"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))
    implementation(project(":core:data"))
    implementation(project(":core:network"))
    implementation(project(":core:secrets"))
    implementation(project(":feature:auth:domain"))
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    testImplementation(libs.okhttp)
}
