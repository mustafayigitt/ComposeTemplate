plugins {
    id("composetemplate.android.library")
    id("composetemplate.android.hilt")
    id("composetemplate.android.room")
}

android {
    namespace = "com.ytapps.composetemplate.core.database"
}

dependencies {
    implementation(project(":core:common"))
}
