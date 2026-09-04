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
 * Scans one module's Kotlin sources and reports every import that reaches into a module it is
 * not allowed to name.
 *
 * The task knows nothing about which module it is checking. Everything specific to a module
 * arrives as an input: which import prefixes are guarded at all, which of them are permitted
 * anyway, and what advice to print when the rule is broken. That is what lets the same
 * implementation serve `:app`, a core module and a feature module without three copies of the
 * scanning logic.
 *
 * Imports are matched textually on purpose. The check has to run before compilation, because
 * after compilation the failure it prevents has already happened.
 */
abstract class CheckModuleBoundaryTask : DefaultTask() {
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sources: ConfigurableFileCollection

    /** How this module is named in failure messages, normally its Gradle path. */
    @get:Input
    abstract val moduleLabel: Property<String>

    /** The module's own package root, used to tell project imports apart from third-party ones. */
    @get:Input
    abstract val moduleNamespace: Property<String>

    /** The module's Gradle path segments, used to trim the namespace back to the package root. */
    @get:Input
    abstract val modulePathSegments: ListProperty<String>

    /** Import prefixes, relative to the package root, that this module is checked against. */
    @get:Input
    abstract val guardedPrefixes: ListProperty<String>

    /** Guarded imports that are allowed anyway. `*` matches exactly one package segment. */
    @get:Input
    abstract val permittedPatterns: ListProperty<String>

    /** Module-specific guidance appended to the failure message. */
    @get:Input
    abstract val adviceLines: ListProperty<String>

    @get:OutputFile
    abstract val reportFile: RegularFileProperty

    @TaskAction
    fun verify() {
        val label = moduleLabel.get()
        val namespace = moduleNamespace.get()
        if (namespace.isBlank()) {
            logger.warn("Skipping the boundary check for $label: its namespace is not set.")
            writeReport("skipped: the namespace of $label could not be resolved")
            return
        }

        val kotlinSources =
            sources.asFileTree.files
                .filter { file -> file.isFile && file.extension == "kt" }
                .sortedBy { file -> file.invariantSeparatorsPath }

        val projectPrefix = rootPackageOf(namespace) + "."
        val guarded = guardedPrefixes.get()
        val permitted = permittedPatterns.get().map { pattern -> compilePattern(pattern) }

        val violations =
            kotlinSources.flatMap { file ->
                violationsIn(file, projectPrefix, guarded, permitted)
            }

        if (violations.isNotEmpty()) {
            logger.error(buildFailureMessage(label, violations))
            writeReport(violations.joinToString(separator = "\n"))
            throw GradleException(
                "$label imports ${violations.size} symbol(s) from modules it is not allowed to name.",
            )
        }

        writeReport("ok: ${kotlinSources.size} file(s) checked, no forbidden imports")
        logger.lifecycle("Boundary check complete for $label: ${kotlinSources.size} file(s) checked.")
    }

    /**
     * Trims the module path off the namespace so imports can be compared against `core.` and
     * `feature.` prefixes.
     *
     * `:core:ui` declares the namespace `<root>.core.ui`, so the root package is what remains
     * after removing `core.ui`. The suffix is only removed when it is actually present: a module
     * whose namespace does not mirror its folder path keeps the namespace as its root and simply
     * guards fewer imports, which fails open rather than reporting nonsense.
     */
    private fun rootPackageOf(namespace: String): String {
        val suffix = modulePathSegments.get().joinToString(separator = ".")
        if (suffix.isEmpty()) return namespace
        return namespace.removeSuffix(".$suffix")
    }

    private fun violationsIn(
        file: File,
        projectPrefix: String,
        guarded: List<String>,
        permitted: List<Regex>,
    ): List<String> =
        file.readLines().mapIndexedNotNull { index, rawLine ->
            val imported = importedNameOrNull(rawLine) ?: return@mapIndexedNotNull null
            if (!imported.startsWith(projectPrefix)) return@mapIndexedNotNull null

            val relative = imported.removePrefix(projectPrefix)
            val isGuarded = guarded.any { prefix -> relative.startsWith(prefix) }
            val isPermitted = permitted.any { pattern -> pattern.matches(relative) }

            if (isGuarded && !isPermitted) "${file.name}:${index + 1}  import $imported" else null
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

    /**
     * Turns a permitted pattern into an anchored regex, where `*` stands for exactly one package
     * segment. `feature.*.navigation.` therefore matches any feature's navigation module without
     * naming the features, which is what keeps the rule from needing an edit per feature.
     */
    private fun compilePattern(pattern: String): Regex {
        val expression =
            pattern
                .split(SEGMENT_WILDCARD)
                .joinToString(separator = SEGMENT_EXPRESSION) { part -> Regex.escape(part) }
        return Regex("^$expression.*")
    }

    private fun buildFailureMessage(
        label: String,
        violations: List<String>,
    ): String =
        buildString {
            appendLine()
            appendLine("$label imports symbols from modules it is not allowed to name:")
            appendLine()
            violations.forEach { violation -> appendLine("  $violation") }
            appendLine()
            adviceLines.get().forEach { advice -> appendLine("  $advice") }
            appendLine()
            appendLine("  Removing a module has to stay a folder deletion. An import turns it into a")
            appendLine("  compile error, which is exactly what this check prevents.")
            appendLine()
        }

    private fun writeReport(summary: String) {
        val report = reportFile.get().asFile
        report.parentFile?.mkdirs()
        report.writeText(summary + "\n")
    }

    private companion object {
        const val IMPORT_PREFIX = "import "
        const val ALIAS_SEPARATOR = " as "
        const val SEGMENT_WILDCARD = "*"
        const val SEGMENT_EXPRESSION = "[^.]+"
    }
}
