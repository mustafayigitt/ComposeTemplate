package com.ytapps.composetemplate.convention

import com.android.build.api.dsl.TestExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class BaselineProfileGeneratorConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.test")

            extensions.configure<TestExtension> {
                compileSdk = libs.findVersion("compileSdk").get().toString().toInt()
                defaultConfig {
                    minSdk = libs.findVersion("minSdk").get().toString().toInt()
                }
                targetProjectPath = ":app"
                experimentalProperties["android.experimental.self-instrumenting"] = true
            }

            dependencies {
                add("implementation", libs.findLibrary("androidx-benchmark-macro-junit4").get())
                add("implementation", libs.findLibrary("androidx-uiautomator").get())
                add("implementation", libs.findLibrary("androidx-junit").get())
            }
        }
    }
}
