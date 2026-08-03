plugins {
    id("composetemplate.feature.presentation")
}

android {
    namespace = "com.ytapps.composetemplate.feature.profile.presentation"
}

dependencies {
    implementation(project(":feature:profile:domain"))
    implementation(project(":feature:profile:navigation"))
    implementation(libs.androidx.appcompat)
}
