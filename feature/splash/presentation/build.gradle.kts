plugins {
    id("composetemplate.feature.presentation")
}

android {
    namespace = "com.ytapps.composetemplate.feature.splash.presentation"
}

dependencies {
    implementation(project(":feature:splash:domain"))
    implementation(project(":feature:splash:navigation"))
    implementation(project(":feature:home:navigation"))
    implementation(project(":feature:auth:navigation"))
}
