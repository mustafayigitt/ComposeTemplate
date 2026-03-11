plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.library.compose")
    id("composetemplate.android.hilt")
    id("composetemplate.feature")
}

android {
    namespace = "com.lhacenmed.budget.feature.home.presentation"
}

dependencies {
    implementation(project(":feature:home:domain"))
    implementation(project(":feature:home:navigation"))
    implementation(project(":feature:list:navigation"))
}
