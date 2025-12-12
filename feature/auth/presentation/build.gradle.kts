plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.library.compose")
    id("composetemplate.android.hilt")
    id("composetemplate.feature")
    id("composetemplate.test")
}

android {
    namespace = "com.ytapps.composetemplate.feature.auth.presentation"
}

dependencies {
    implementation(project(":feature:auth:domain"))
    implementation(project(":feature:auth:navigation"))
    implementation(project(":feature:splash:navigation"))
}
