plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.library.compose")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.lhacenmed.budget.feature.profile.presentation"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":contract"))
    implementation(project(":feature:profile:domain"))
    implementation(project(":feature:profile:navigation"))
}
