plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.library.compose")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.ytapps.composetemplate.feature.splash.presentation"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":contract"))
    implementation(project(":feature:splash:domain"))
    implementation(project(":feature:splash:navigation"))
    implementation(project(":feature:home:navigation"))
    implementation(project(":feature:auth:navigation"))
}
