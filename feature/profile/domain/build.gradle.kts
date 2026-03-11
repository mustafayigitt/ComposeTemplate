plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.lhacenmed.budget.feature.profile.domain"
}

dependencies {
    implementation(project(":core"))
}
