plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.ytapps.composetemplate.feature.profile.data"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":feature:profile:domain"))
}
