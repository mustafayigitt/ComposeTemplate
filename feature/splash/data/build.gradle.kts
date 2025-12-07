plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.ytapps.composetemplate.feature.splash.data"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":feature:splash:domain"))
}
