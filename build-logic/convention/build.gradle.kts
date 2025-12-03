plugins {
    `kotlin-dsl`
}

group = "com.ytapps.composetemplate.convention"

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
    compileOnly(libs.compose.compiler.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "composetemplate.android.application"
            implementationClass = "com.ytapps.composetemplate.convention.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "composetemplate.android.library"
            implementationClass = "com.ytapps.composetemplate.convention.AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "composetemplate.android.application.compose"
            implementationClass = "com.ytapps.composetemplate.convention.AndroidComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "composetemplate.android.hilt"
            implementationClass = "com.ytapps.composetemplate.convention.AndroidHiltConventionPlugin"
        }
    }
}
