plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.lhacenmed.budget.feature.splash.data"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":feature:splash:domain"))
}
