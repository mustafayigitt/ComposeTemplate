plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.ytapps.composetemplate.feature.detail.data"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":feature:detail:domain"))
}
