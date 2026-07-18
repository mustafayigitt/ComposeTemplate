plugins {
    id("composetemplate.feature.presentation")
}

android {
    namespace = "com.ytapps.composetemplate.feature.onboarding.presentation"
}

dependencies {
    implementation(project(":feature:onboarding:domain"))
    implementation(project(":feature:onboarding:navigation"))
    implementation(project(":feature:auth:navigation"))
}
