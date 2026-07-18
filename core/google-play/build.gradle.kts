plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.library.compose")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.ytapps.composetemplate.core.googleplay"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:config"))
    implementation(libs.timber)
    implementation(libs.google.play.review)
    implementation(libs.google.play.app.update)
}
