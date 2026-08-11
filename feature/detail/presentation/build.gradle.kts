plugins {
    id("composetemplate.feature.presentation")
}

android {
    namespace = "com.ytapps.composetemplate.feature.detail.presentation"
}

dependencies {
    implementation(project(":feature:detail:domain"))
    implementation(project(":feature:detail:navigation"))
}
