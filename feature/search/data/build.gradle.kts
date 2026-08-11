plugins {
    id("composetemplate.feature.data")
}

android {
    namespace = "com.ytapps.composetemplate.feature.search.data"
}

dependencies {
    implementation(project(":feature:search:domain"))
}
