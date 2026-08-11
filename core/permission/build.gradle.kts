plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.library.compose")
}

android {
    namespace = "com.ytapps.composetemplate.core.permission"
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.activity.compose)
}
