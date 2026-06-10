plugins {
    id("composetemplate.feature.presentation")
}

android {
    namespace = "com.ytapps.composetemplate.feature.auth.presentation"
}

dependencies {
    implementation(project(":feature:auth:domain"))
    implementation(project(":feature:auth:navigation"))
    implementation(project(":feature:splash:navigation"))
}
