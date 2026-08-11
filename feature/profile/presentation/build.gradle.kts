plugins {
    id("composetemplate.feature.presentation")
}

android {
    namespace = "com.ytapps.composetemplate.feature.profile.presentation"
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":feature:auth:navigation"))
    implementation(project(":feature:profile:domain"))
    implementation(project(":feature:profile:navigation"))
}
