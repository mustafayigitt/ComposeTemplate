plugins {
    id("composetemplate.android.library")
}

android {
    namespace = "com.ytapps.composetemplate.core.security"
}

dependencies {
    implementation(project(":core:secrets"))
    implementation(libs.androidx.core)
}
