package com.ytapps.composetemplate.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class FeatureNavigationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("composetemplate.android.library")
            pluginManager.apply("composetemplate.android.library.compose")
            pluginManager.apply("composetemplate.android.hilt")

            dependencies {
                add("implementation", project(":core:common"))
                add("implementation", project(":core:navigation"))
                add("implementation", libs.findLibrary("androidx-material-icons-core").get())
            }
        }
    }
}
