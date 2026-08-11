plugins {
    id("composetemplate.feature.presentation")
}

android {
    namespace = "com.ytapps.composetemplate.feature.list.presentation"
}

dependencies {
    implementation(project(":feature:list:domain"))
    implementation(project(":feature:list:navigation"))
    implementation(project(":feature:detail:navigation"))
}
