plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.library.compose")
}

android {
    namespace = "com.lhacenmed.budget.contract"
}

dependencies {
    implementation(project(":core"))
    implementation(libs.androidx.core)
    implementation(libs.androidx.compose.bom)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.kotlinx.serialization.core)
}
