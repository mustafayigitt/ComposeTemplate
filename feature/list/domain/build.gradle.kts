plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.ytapps.composetemplate.feature.list.domain"
}

dependencies {
    implementation(project(":core"))
}
