package com.ytapps.composetemplate.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class FeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                add("implementation", project(":core"))
                add("implementation", project(":contract"))
            }
        }
    }
}
