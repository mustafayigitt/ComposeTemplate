plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
}

android {
    namespace = "com.ytapps.composetemplate.core.analytics"
}

dependencies {
    implementation(project(":core:common"))
    // Needed only to contribute a NavigationObserver; the dependency points at a core module,
    // never the other way around, so core:analytics stays deletable.
    implementation(project(":core:navigation"))
    implementation(libs.timber)
}
