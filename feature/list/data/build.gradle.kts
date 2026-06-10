plugins {
    id("composetemplate.feature.data")
}

android {
    namespace = "com.ytapps.composetemplate.feature.list.data"
}

dependencies {
    implementation(project(":feature:list:domain"))
}
