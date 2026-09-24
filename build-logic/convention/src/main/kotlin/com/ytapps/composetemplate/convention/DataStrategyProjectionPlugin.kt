package com.ytapps.composetemplate.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class DataStrategyProjectionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        var existingSiblingDirectories = emptySet<File>()
        target.tasks.named("create" + "-new-app").configure {
            doFirst {
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
                val strategy = DataStrategy.parse(target.findProperty("dataStrategy")?.toString())
                val requestedSecrets =
                    target.findProperty("withSecrets")?.toString()?.toBooleanStrictOrNull() ?: true
                DataStrategyProjector.project(
                    targetDir = targetDir,
                    strategy = strategy,
                    includeSecrets = requestedSecrets && strategy.usesNetwork,
                )
            }
        }
    }
}
