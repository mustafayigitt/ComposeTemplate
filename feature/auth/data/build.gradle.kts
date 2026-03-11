plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
    id("composetemplate.test")
}

android {
    namespace = "com.lhacenmed.budget.feature.auth.data"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":feature:auth:domain"))
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

}
