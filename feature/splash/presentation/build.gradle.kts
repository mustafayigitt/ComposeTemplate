plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.library.compose")
    id("composetemplate.android.hilt")
    id("composetemplate.test")
}

android {
    namespace = "com.ytapps.composetemplate.feature.splash.presentation"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))
    implementation(project(":core:data"))
    implementation(project(":core:network"))
    implementation(project(":core:secrets"))
    implementation(project(":contract"))
    implementation(project(":feature:splash:domain"))
    implementation(project(":feature:splash:navigation"))
    implementation(project(":feature:home:navigation"))
    implementation(project(":feature:auth:navigation"))
}
