plugins {
    id("composetemplate.feature.data")
}

android {
    namespace = "com.ytapps.composetemplate.feature.home.data"
}

dependencies {
    implementation(project(":feature:home:domain"))
}
