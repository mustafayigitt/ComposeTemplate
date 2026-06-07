plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.library.native")
}

android {
    namespace = "com.ytapps.composetemplate.core.secrets"
}

dependencies {
    implementation(project(":core:common"))
}
