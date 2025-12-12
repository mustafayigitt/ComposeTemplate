package com.ytapps.composetemplate.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
            val extension = extensions.getByType(CommonExtension::class.java)
            configureAndroidCompose(extension)
        }
    }
}

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }
    }

    dependencies {
        val bom = libs.findLibrary("androidx-compose-bom").get()
        add("implementation", platform(bom))
        add("androidTestImplementation", platform(bom))

        // Compose UI
        add("implementation", libs.findLibrary("androidx-ui").get())
        add("implementation", libs.findLibrary("androidx-ui-graphics").get())
        add("implementation", libs.findLibrary("androidx-ui-tooling-preview").get())
        add("implementation", libs.findLibrary("androidx-material3").get())

        // Debug tooling
        add("debugImplementation", libs.findLibrary("androidx-ui-tooling").get())
        add("debugImplementation", libs.findLibrary("androidx-ui-test-manifest").get())

        // Test
        add("androidTestImplementation", libs.findLibrary("androidx-ui-test-junit4").get())
    }
}
