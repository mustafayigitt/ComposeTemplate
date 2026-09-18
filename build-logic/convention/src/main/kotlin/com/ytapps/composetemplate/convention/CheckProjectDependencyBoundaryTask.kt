package com.ytapps.composetemplate.convention

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Checks literal project dependencies declared in one module's build script.
 *
 * This is deliberately a source-level check rather than a walk over resolved Gradle
 * configurations. `:app` intentionally wires every discovered core and feature module with a
 * dynamic `project(path)` call; treating that aggregator as an ordinary dependency edge would
 * reject the very module-discovery mechanism that makes the template pleasant to extend.
 *
 * Literal `project(":...")` references are different: they make a specific module part of the
 * importing module's build-file contract and therefore need the same boundary rule as a Kotlin
 * import. The scanner understands the normal Kotlin DSL forms, ignores comments, and fails open
 * for dynamic paths it cannot prove statically rather than rejecting a valid extension.
 */
abstract class CheckProjectDependencyBoundaryTask : DefaultTask() {
    @get:InputFile
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val buildFile: RegularFileProperty

    @get:Input
    abstract val moduleLabel: Property<String>

    /** Gradle project-path patterns; `*` matches exactly one `:`-delimited segment. */
    @get:Input
    abstract val permittedPatterns: ListProperty<String>

    @get:OutputFile
    abstract val reportFile: RegularFileProperty

    @TaskAction
    fun verify() {
        val label = moduleLabel.get()
        val references = projectReferences(buildFile.get().asFile)
        val permitted = permittedPatterns.get().map(::compilePattern)
        val violations = references.filter { reference ->
            permitted.none { pattern -> pattern.matches(reference.path) }
        }

        if (violations.isNotEmpty()) {
            writeReport(violations.joinToString(separator = "\n") { it.display })
            throw GradleException(buildFailureMessage(label, violations))
        }

        writeReport("ok: ${references.size} literal project dependency reference(s) checked")
        logger.lifecycle(
            "Project dependency boundary complete for $label: " +
                "${references.size} literal reference(s) checked.",
        )
    }

    private fun projectReferences(file: File): List<ProjectReference> {
        val lines = removeComments(file.readLines())
        return lines.flatMapIndexed { index, line ->
            PROJECT_REFERENCE_PATTERN.findAll(line).map { match ->
                ProjectReference(
                    path = match.groupValues[1],
                    display = "${file.name}:${index + 1}  project(\"${match.groupValues[1]}\")",
                )
            }.toList()
        }
    }

    /**
     * Removes Kotlin line and block comments while preserving line boundaries for useful errors.
     * String contents are retained because the path argument itself is a string literal.
     */
    private fun removeComments(lines: List<String>): List<String> {
        var inBlockComment = false
        return lines.map { line ->
            buildString {
                var index = 0
                var inString = false
                while (index < line.length) {
                    if (inBlockComment) {
                        val end = line.indexOf("*/", index)
                        if (end < 0) break
                        inBlockComment = false
                        index = end + 2
                        continue
                    }

                    if (!inString && line.startsWith("//", index)) break
                    if (!inString && line.startsWith("/*", index)) {
                        inBlockComment = true
                        index += 2
                        continue
                    }

                    val character = line[index]
                    append(character)
                    if (character == '"' && (index == 0 || line[index - 1] != '\\')) {
                        inString = !inString
                    }
                    index++
                }
            }
        }
    }

    private fun compilePattern(pattern: String): Regex {
        val expression =
            pattern
                .split(SEGMENT_WILDCARD)
                .joinToString(separator = SEGMENT_EXPRESSION) { part -> Regex.escape(part) }
        return Regex("^$expression$")
    }

    private fun buildFailureMessage(
        label: String,
        violations: List<ProjectReference>,
    ): String =
        buildString {
            appendLine(
                "$label declares ${violations.size} project dependency edge(s) " +
                    "that it is not allowed to name:",
            )
            appendLine()
            violations.forEach { violation -> appendLine("  ${violation.display}") }
            appendLine()
            appendLine("  Allowed literal project dependency patterns:")
            permittedPatterns.get().forEach { pattern -> appendLine("    $pattern") }
            appendLine()
            appendLine("  The rule checks explicit project(\":...\") references only.")
            append("  Dynamic project paths remain available for module discovery and extensions.")
        }

    private fun writeReport(summary: String) {
        val report = reportFile.get().asFile
        report.parentFile?.mkdirs()
        report.writeText(summary + "\n")
    }

    private data class ProjectReference(
        val path: String,
        val display: String,
    )

    private companion object {
        val PROJECT_REFERENCE_PATTERN =
            Regex("""\bproject\s*\(\s*(?:path\s*=\s*)?[\"'](:[^\"']+)[\"']""")
        const val SEGMENT_WILDCARD = "*"
        const val SEGMENT_EXPRESSION = "[^:]+"
    }
}
