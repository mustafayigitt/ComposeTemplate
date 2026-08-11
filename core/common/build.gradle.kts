plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.ytapps.composetemplate.core.common"
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.timber)
}
