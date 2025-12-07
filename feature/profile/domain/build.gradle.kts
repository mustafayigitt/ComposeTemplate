plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.ytapps.composetemplate.feature.profile.domain"
}

dependencies {
    implementation(project(":core"))
}
