plugins {
    id("composetemplate.feature.data")
}

android {
    namespace = "com.ytapps.composetemplate.feature.splash.data"
}

dependencies {
    implementation(project(":feature:splash:domain"))
}
