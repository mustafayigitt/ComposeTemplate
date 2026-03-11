plugins {
    `kotlin-dsl`
}

group = "com.lhacenmed.budget.convention"

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
    compileOnly(libs.compose.compiler.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("createNewApp") {
            id = "composetemplate.create.new.app"
            implementationClass = "com.lhacenmed.budget.convention.CreateNewAppPlugin"
        }
        register("androidApplication") {
            id = "composetemplate.android.application"
            implementationClass = "com.lhacenmed.budget.convention.AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "composetemplate.android.application.compose"
            implementationClass = "com.lhacenmed.budget.convention.AndroidComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "composetemplate.android.library"
            implementationClass = "com.lhacenmed.budget.convention.AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "composetemplate.android.library.compose"
            implementationClass = "com.lhacenmed.budget.convention.AndroidComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "composetemplate.android.hilt"
            implementationClass = "com.lhacenmed.budget.convention.AndroidHiltConventionPlugin"
        }
        register("test") {
            id = "composetemplate.test"
            implementationClass = "com.lhacenmed.budget.convention.TestConventionPlugin"
        }
        register("feature") {
            id = "composetemplate.feature"
            implementationClass = "com.lhacenmed.budget.convention.FeatureConventionPlugin"
        }
    }
}
