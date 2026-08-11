plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.ytapps.composetemplate.core.analytics"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.timber)
}
