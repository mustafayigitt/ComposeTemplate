plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
    id("composetemplate.test")
}

android {
    namespace = "com.lhacenmed.budget.feature.auth.domain"
}

dependencies {
    implementation(project(":core"))
}
