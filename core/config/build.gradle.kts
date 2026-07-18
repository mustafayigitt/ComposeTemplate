plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.ytapps.composetemplate.core.config"
}

dependencies {
    implementation(project(":core:common"))
}
