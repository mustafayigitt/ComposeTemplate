plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
    id("composetemplate.test")
}

android {
    namespace = "com.ytapps.composetemplate.core.network"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:secrets"))
    implementation(project(":core:data"))
    implementation(libs.androidx.core)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.timber)
}
