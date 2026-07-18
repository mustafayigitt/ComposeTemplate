plugins {
    id("composetemplate.feature.data")
}

android {
    namespace = "com.ytapps.composetemplate.feature.onboarding.data"
}

dependencies {
    implementation(project(":feature:onboarding:domain"))
}
