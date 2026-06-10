plugins {
    id("composetemplate.feature.data")
}

android {
    namespace = "com.ytapps.composetemplate.feature.detail.data"
}

dependencies {
    implementation(project(":feature:detail:domain"))
}
