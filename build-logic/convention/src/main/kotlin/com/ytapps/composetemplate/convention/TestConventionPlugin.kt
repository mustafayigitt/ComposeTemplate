package com.ytapps.composetemplate.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.register
import org.gradle.testing.jacoco.tasks.JacocoReport

class TestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("jacoco")

            dependencies {
                add("testImplementation", libs.findLibrary("junit").get())
                add("testImplementation", libs.findLibrary("truth").get())
                add("testImplementation", libs.findLibrary("mockk").get())
                add("testImplementation", libs.findLibrary("kotlinx-coroutines-test").get())

                add("androidTestImplementation", libs.findLibrary("androidx-junit").get())
                add("androidTestImplementation", libs.findLibrary("androidx-espresso-core").get())
            }

            tasks.register<JacocoReport>("jacocoDebugReport") {
                dependsOn("testDebugUnitTest")
                executionData(
                    fileTree(layout.buildDirectory) {
                        include("jacoco/testDebugUnitTest.exec")
                        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
                    },
                )
                sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))
                classDirectories.setFrom(
                    fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug")) {
                        exclude(JACOCO_EXCLUSIONS)
                    },
                    fileTree(layout.buildDirectory.dir("intermediates/javac/debug/classes")) {
                        exclude(JACOCO_EXCLUSIONS)
                    },
                )
                reports {
                    xml.required.set(true)
                    html.required.set(true)
                }
                onlyIf { executionData.files.any { it.exists() } }
            }
        }
    }

    private companion object {
        val JACOCO_EXCLUSIONS =
            listOf(
                "**/R.class",
                "**/R$*.class",
                "**/BuildConfig.*",
                "**/*_Factory.*",
                "**/*_HiltModules*.*",
                "**/*Hilt*.*",
                "**/*ComposableSingletons*.*",
            )
    }
}
