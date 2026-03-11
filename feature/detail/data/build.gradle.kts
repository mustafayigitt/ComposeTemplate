plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.lhacenmed.budget.feature.detail.data"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":feature:detail:domain"))
}
