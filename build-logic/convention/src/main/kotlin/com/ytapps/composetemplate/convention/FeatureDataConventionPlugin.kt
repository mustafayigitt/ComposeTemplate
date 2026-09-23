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

            val optionalInfrastructure =
                listOf(":core:network", ":core:database")
                    .mapNotNull(rootProject::findProject)

            dependencies {
                add("implementation", project(":core:common"))
                add("implementation", project(":core:data"))
                optionalInfrastructure.forEach { infrastructure ->
                    add("implementation", infrastructure)
                }
            }
        }
    }
}
