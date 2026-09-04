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
    // TEMPORARY probe dependency, reverted in the next commit. core:data survives every
    // plug-out combination, so naming an optional module here is exactly the violation
    // checkModuleBoundary is supposed to reject. core:config is used rather than
    // core:network because core:network already depends on core:data, and the reverse edge
    // made Gradle reject the task graph before any check could run.
    implementation(project(":core:config"))
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.appcompat)
}
