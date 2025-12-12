plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.library.compose")
    id("composetemplate.android.hilt")
    id("composetemplate.feature")
}

android {
    namespace = "com.ytapps.composetemplate.feature.list.presentation"
}

dependencies {
    implementation(project(":feature:list:domain"))
    implementation(project(":feature:list:navigation"))
    implementation(project(":feature:detail:navigation"))
}
