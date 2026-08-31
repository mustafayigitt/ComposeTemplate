plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.library.native")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.ytapps.composetemplate.core.secrets"
}

dependencies {
    implementation(project(":core:common"))
}
