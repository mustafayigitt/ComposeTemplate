plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.library.compose")
}

android {
    namespace = "com.ytapps.composetemplate.contract"
}

dependencies {
    implementation(project(":core"))
    implementation(libs.androidx.core)
    implementation(libs.androidx.compose.bom)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    api(libs.androidx.navigation3.runtime)
    implementation(libs.kotlinx.serialization.core)
}
