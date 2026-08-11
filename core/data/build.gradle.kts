plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
    id("composetemplate.test")
}

android {
    namespace = "com.ytapps.composetemplate.core.data"
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.appcompat)
}
