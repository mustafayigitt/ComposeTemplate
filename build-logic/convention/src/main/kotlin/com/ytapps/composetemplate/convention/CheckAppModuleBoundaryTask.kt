package com.ytapps.composetemplate.convention

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Scans the application module's Kotlin sources and reports every import that reaches into a
 * module which is allowed to be plugged out.
 *
 * Imports are matched textually on purpose. The check has to run before compilation, because
 * after compilation the failure it prevents has already happened.
 */
abstract class CheckAppModuleBoundaryTask : DefaultTask() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sources: ConfigurableFileCollection

    /** Core module names, without the `core.` prefix, that `:app` may import from. */
    @get:Input
    abstract val permittedCoreModules: ListProperty<String>

    /** The module's package root, used to tell project imports apart from third-party ones. */
    @get:Input
    abstract val applicationPackage: Property<String>

    @get:OutputFile
    abstract val reportFile: RegularFileProperty

    @TaskAction
    fun verify() {
        val basePackage = applicationPackage.get()
        if (basePackage.isBlank()) {
            logger.warn("Skipping the app module boundary check: the application namespace is not set.")
            writeReport("skipped: the application namespace could not be resolved")
            return
        }

        val kotlinSources =
            sources.asFileTree.files
                .filter { file -> file.isFile && file.extension == "kt" }
                .sortedBy { file -> file.invariantSeparatorsPath }

        val violations = kotlinSources.flatMap { file -> violationsIn(file, basePackage) }

        if (violations.isNotEmpty()) {
            logger.error(buildFailureMessage(violations))
            writeReport(violations.joinToString(separator = "\n"))
            throw GradleException(
                "The application module imports ${violations.size} symbol(s) from modules that must stay removable.",
            )
        }

        writeReport("ok: ${kotlinSources.size} file(s) checked, no imports from optional modules")
        logger.lifecycle("App module boundary check complete: ${kotlinSources.size} file(s) checked.")
    }

    private fun violationsIn(
        file: File,
        basePackage: String,
    ): List<String> {
        val projectPrefix = "$basePackage."
        val permitted = permittedCoreModules.get().map { module -> "core.$module." }

        return file.readLines().mapIndexedNotNull { index, rawLine ->
            val imported = importedNameOrNull(rawLine) ?: return@mapIndexedNotNull null
            if (!imported.startsWith(projectPrefix)) return@mapIndexedNotNull null

            val relative = imported.removePrefix(projectPrefix)
            val forbidden =
                when {
                    relative.startsWith(FEATURE_PREFIX) -> true
                    relative.startsWith(CORE_PREFIX) -> permitted.none { allowed -> relative.startsWith(allowed) }
                    else -> false
                }

            if (forbidden) "${file.name}:${index + 1}  import $imported" else null
        }
    }

    private fun importedNameOrNull(rawLine: String): String? {
        val line = rawLine.trim()
        if (!line.startsWith(IMPORT_PREFIX)) return null
        return line
            .removePrefix(IMPORT_PREFIX)
            .substringBefore(ALIAS_SEPARATOR)
            .trim()
            .takeUnless { candidate -> candidate.isEmpty() }
    }

    private fun buildFailureMessage(violations: List<String>): String {
        val permitted = permittedCoreModules.get().joinToString(separator = ", ") { module -> "core.$module" }
        return buildString {
            appendLine()
            appendLine("The application module imports symbols from modules that must stay removable:")
            appendLine()
            violations.forEach { violation -> appendLine("  $violation") }
            appendLine()
            appendLine("  :app may import these core modules only: $permitted - plus its own packages.")
            appendLine()
            appendLine("  Optional modules must reach the app through a multibinding, not an import:")
            appendLine("    startup work      -> contribute an AppInitializer")
            appendLine("    navigation events -> contribute a NavigationObserver")
            appendLine("    a misplaced type  -> move it into core:common")
            appendLine()
            appendLine("  Removing an optional module has to stay a folder deletion. An import turns")
            appendLine("  it into a compile error, which is exactly what this check prevents.")
            appendLine()
        }
    }

    private fun writeReport(summary: String) {
        val report = reportFile.get().asFile
        report.parentFile?.mkdirs()
        report.writeText(summary + "\n")
    }

    private companion object {
        const val IMPORT_PREFIX = "import "
        const val ALIAS_SEPARATOR = " as "
        const val CORE_PREFIX = "core."
        const val FEATURE_PREFIX = "feature."
    }
}
