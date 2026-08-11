package com.ytapps.composetemplate.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class FeatureDomainConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("composetemplate.android.library")
            pluginManager.apply("composetemplate.android.hilt")
            pluginManager.apply("composetemplate.test")

            dependencies {
                add("implementation", project(":core:common"))
            }
        }
    }
}
