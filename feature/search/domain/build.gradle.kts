plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.lhacenmed.budget.feature.search.domain"
}

dependencies {
    implementation(project(":core"))
}
