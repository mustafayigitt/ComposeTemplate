package com.ytapps.composetemplate.convention

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class DataStrategyProjectionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        var existingSiblingDirectories = emptySet<File>()
        lateinit var strategy: DataStrategy
        var includeSecrets = true

        target.tasks.named("create" + "-new-app").configure {
            doFirst {
                strategy = DataStrategy.parse(target.findProperty("dataStrategy")?.toString())
                val requestedSecrets =
                    target.findProperty("withSecrets")?.toString()?.let { value ->
                        value.toBooleanStrictOrNull()
                            ?: throw GradleException("-PwithSecrets must be either true or false, but was '$value'.")
                    } ?: true
                includeSecrets = requestedSecrets && strategy.usesNetwork
                existingSiblingDirectories =
                    target.rootDir.parentFile.listFiles()
                        ?.filter(File::isDirectory)
                        ?.toSet()
                        .orEmpty()
            }
            doLast {
                val generatedDirectories =
                    target.rootDir.parentFile.listFiles()
                        ?.filter(File::isDirectory)
                        ?.filterNot(existingSiblingDirectories::contains)
                        .orEmpty()
                val targetDir = generatedDirectories.singleOrNull() ?: return@doLast
                DataStrategyProjector.project(targetDir, strategy, includeSecrets)
            }
        }
    }
}
