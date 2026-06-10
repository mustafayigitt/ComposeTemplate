plugins {
    id("composetemplate.feature.data")
}

android {
    namespace = "com.ytapps.composetemplate.feature.profile.data"
}

dependencies {
    implementation(project(":feature:profile:domain"))
}
