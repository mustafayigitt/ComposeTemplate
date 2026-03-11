plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.lhacenmed.budget.feature.list.data"
}

dependencies {
    implementation(project(":core"))
    implementation(project(":feature:list:domain"))
}
