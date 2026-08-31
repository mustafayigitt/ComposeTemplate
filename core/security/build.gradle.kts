plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.ytapps.composetemplate.core.security"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:secrets"))
    implementation(libs.androidx.core)
    implementation(libs.timber)
}
