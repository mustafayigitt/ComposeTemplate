plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.lhacenmed.budget.feature.search.data"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":feature:search:domain"))
}
