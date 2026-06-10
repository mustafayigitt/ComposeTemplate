plugins {
    id("composetemplate.feature.presentation")
}

android {
    namespace = "com.ytapps.composetemplate.feature.home.presentation"
}

dependencies {
    implementation(project(":feature:home:domain"))
    implementation(project(":feature:home:navigation"))
    implementation(project(":feature:list:navigation"))
}
