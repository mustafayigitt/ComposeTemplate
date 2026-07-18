package com.ytapps.composetemplate.convention

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class ScaffoldFeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.register("scaffoldFeature") {
            group = "setup"
            description = "Scaffolds a new feature module with data, domain, navigation, presentation sub-modules"

            doLast {
                val featureName = target.findProperty("featureName")?.toString()
                    ?: throw IllegalArgumentException("Required: -PfeatureName=<name> (e.g., -PfeatureName=settings)")

                val pkg = target.findProperty("featurePkg")?.toString()
                    ?: "com.ytapps.composetemplate.feature"

                val featureDir = File(target.rootProject.rootDir, "feature/$featureName")
                if (featureDir.exists()) {
                    throw GradleException("Feature '$featureName' already exists at ${featureDir.absolutePath}")
                }

                featureDir.mkdirs()

                val subModules = listOf("domain", "data", "navigation", "presentation")
                val featurePkg = "$pkg.$featureName"

                subModules.forEach { module ->
                    val moduleDir = File(featureDir, module)
                    moduleDir.mkdirs()

                    val srcDir = File(moduleDir, "src/main/java")
                    val srcPath = featurePkg.replace('.', File.separatorChar)
                    File(srcDir, srcPath).mkdirs()

                    val buildGradle = getBuildGradleContent(featureName, module, pkg)
                    File(moduleDir, "build.gradle.kts").writeText(buildGradle)

                    if (module == "navigation") {
                        val routeFile = File(File(srcDir, srcPath), "${featureName.replaceFirstChar { it.uppercase() }}Route.kt")
                        routeFile.writeText(getNavigationContent(featureName, featurePkg))
                    }
                    if (module == "domain") {
                        val domainDir = File(srcDir, srcPath + File.separator + "domain")
                        domainDir.mkdirs()
                        File(domainDir, "PlaceholderUseCase.kt").writeText(getDomainContent(featurePkg))
                    }

                    if (module == "presentation") {
                        val resDir = File(moduleDir, "src/main/res")
                        val valuesDir = File(resDir, "values")
                        val valuesTrDir = File(resDir, "values-tr")
                        valuesDir.mkdirs()
                        valuesTrDir.mkdirs()

                        File(valuesDir, "strings.xml").writeText("<resources>\n    <string name=\"feature_${featureName}_title\">${featureName.replaceFirstChar { it.uppercase() }}</string>\n</resources>")
                        File(valuesTrDir, "strings.xml").writeText("<resources>\n    <string name=\"feature_${featureName}_title\">${featureName.replaceFirstChar { it.uppercase() }}</string>\n</resources>")
                    }

                    if (module == "data" && target.findProperty("withDatabase")?.toString()?.toBoolean() == true) {
                        val daoDir = File(srcDir, srcPath + File.separator + "data" + File.separator + "dao")
                        daoDir.mkdirs()
                        // Placeholder for DAO
                    }
                }

                // Register in settings.gradle.kts
                val settingsFile = File(target.rootProject.rootDir, "settings.gradle.kts")
                if (settingsFile.exists()) {
                    val settingsContent = settingsFile.readText()
                    val newIncludes = subModules.joinToString("\n") { "include(\":feature:$featureName:$it\")" }
                    if (!settingsContent.contains(":feature:$featureName:")) {
                        settingsFile.appendText("\n$newIncludes")
                    }
                }

                // Register in app/build.gradle.kts
                val appBuildFile = File(target.rootProject.rootDir, "app/build.gradle.kts")
                if (appBuildFile.exists()) {
                    val appBuildContent = appBuildFile.readLines().toMutableList()
                    val dependenciesIndex = appBuildContent.indexOfLast { it.trim().startsWith("implementation(project(\":feature:") }
                    if (dependenciesIndex != -1) {
                        val newDeps = subModules.map { "    implementation(project(\":feature:$featureName:$it\"))" }
                        appBuildContent.addAll(dependenciesIndex + 1, newDeps)
                        appBuildFile.writeText(appBuildContent.joinToString("\n"))
                    }
                }

                logger.lifecycle(
                    """
                    |
                    |✅ Feature '$featureName' scaffolded at feature/$featureName/
                    |
                    |Next steps:
                    |  1. Add to settings.gradle.kts:
                    |     include(":feature:$featureName:domain")
                    |     include(":feature:$featureName:data")
                    |     include(":feature:$featureName:navigation")
                    |     include(":feature:$featureName:presentation")
                    |  2. Add dependencies to app/build.gradle.kts:
                    |     implementation(project(":feature:$featureName:data"))
                    |     implementation(project(":feature:$featureName:domain"))
                    |     implementation(project(":feature:$featureName:navigation"))
                    |     implementation(project(":feature:$featureName:presentation"))
                    |
                """.trimMargin()
                )
            }
        }
    }

    private fun getBuildGradleContent(featureName: String, module: String, pkg: String): String {
        val namespace = "$pkg.$featureName.$module"
        return when (module) {
            "domain" -> """plugins {
    id("composetemplate.feature.domain")
}

android {
    namespace = "$namespace"
}
"""
            "data" -> """plugins {
    id("composetemplate.feature.data")
}

android {
    namespace = "$namespace"
}

dependencies {
    implementation(project(":feature:$featureName:domain"))
}
"""
            "navigation" -> """plugins {
    id("composetemplate.feature.navigation")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "$namespace"
}
"""
            "presentation" -> """plugins {
    id("composetemplate.feature.presentation")
}

android {
    namespace = "$namespace"
}

dependencies {
    implementation(project(":feature:$featureName:domain"))
}
"""
            else -> ""
        }
    }

    private fun getNavigationContent(featureName: String, pkg: String): String {
        val routeName = featureName.replaceFirstChar { it.uppercase() }
        return """package $pkg.navigation

import com.ytapps.composetemplate.core.navigation.INavigationItem
import kotlinx.serialization.Serializable

@Serializable
data object ${routeName}Route : INavigationItem {
    override val route: String = "route_$featureName"
}
"""
    }

    private fun getDomainContent(featurePkg: String): String {
        return """package $featurePkg.domain
"""
    }
}
