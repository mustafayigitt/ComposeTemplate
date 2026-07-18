package com.ytapps.composetemplate.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class FeatureDataConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("composetemplate.android.library")
            pluginManager.apply("composetemplate.android.hilt")
            pluginManager.apply("composetemplate.test")

            dependencies {
                add("implementation", project(":core:common"))
                add("implementation", project(":core:data"))
                add("implementation", project(":core:network"))
                add("implementation", project(":core:secrets"))
                add("implementation", project(":core:database"))
            }
        }
    }
}
