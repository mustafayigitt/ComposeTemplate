plugins {
    id("composetemplate.feature.presentation")
}

android {
    namespace = "com.ytapps.composetemplate.feature.search.presentation"
}

dependencies {
    implementation(project(":feature:search:domain"))
    implementation(project(":feature:search:navigation"))
}
