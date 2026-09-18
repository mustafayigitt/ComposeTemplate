package com.ytapps.composetemplate.convention

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty

/**
 * Lets a module widen its own boundary rule from its own build script.
 *
 * The allowlist deliberately lives next to the module it describes. A single shared
 * configuration file would grow one entry per exception and would be read by every module,
 * which is the reason the boundary is a Gradle task rather than a detekt rule in the first
 * place.
 */
abstract class ModuleBoundaryExtension {
    /**
     * Extra import patterns this module may name, relative to the package root, where `*`
     * matches exactly one package segment.
     */
    abstract val additionalPermittedImports: ListProperty<String>

    /**
     * Extra literal Gradle project paths this module may reference from `build.gradle.kts`.
     * `*` matches exactly one `:`-delimited path segment.
     *
     * ```
     * moduleBoundary {
     *     additionalPermittedProjectDependencies.add(":feature:auth:domain")
     * }
     * ```
     */
    abstract val additionalPermittedProjectDependencies: ListProperty<String>
}

/**
 * Applies the plug-out boundary rules to every core and feature module.
 *
 * The source-import rule and the build-file rule are separate tasks because they inspect
 * different coupling surfaces. Both use the same module-derived policy and both are wired into
 * the normal `preBuild`/`check` lifecycle.
 *
 * Every lambda below is parameterless on purpose. Gradle's `Action<T>` parameters surface in
 * Kotlin as `T.() -> Unit`, so declaring a parameter is a compile error. `matching` is the
 * exception: it takes a `Spec<T>`, which stays an ordinary single-argument lambda.
 */
class ModuleBoundaryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension =
            target.extensions.create("moduleBoundary", ModuleBoundaryExtension::class.java)

        val segments = target.path.removePrefix(":").split(":").filter { it.isNotEmpty() }
        val rule = boundaryRuleFor(segments) ?: return

        val boundaryCheck =
            target.tasks.register(
                "checkModuleBoundary",
                CheckModuleBoundaryTask::class.java,
            )

        boundaryCheck.configure {
            group = "verification"
            description = "Fails when ${target.path} imports a module it is not allowed to name"
            moduleLabel.set(target.path)
            sources.from(target.layout.projectDirectory.dir("src"))
            modulePathSegments.set(segments)
            guardedPrefixes.set(rule.guardedPrefixes)
            adviceLines.set(rule.adviceLines)
            reportFile.set(
                target.layout.buildDirectory.file("reports/plugout/module-boundary.txt"),
            )
            permittedPatterns.set(
                target.provider {
                    rule.permittedPatterns + extension.additionalPermittedImports.get()
                },
            )
            moduleNamespace.set(
                target.provider {
                    target.extensions.findByType(LibraryExtension::class.java)
                        ?.namespace
                        .orEmpty()
                },
            )
        }

        val projectDependencyCheck =
            target.tasks.register(
                "checkProjectDependencyBoundary",
                CheckProjectDependencyBoundaryTask::class.java,
            )

        projectDependencyCheck.configure {
            group = "verification"
            description =
                "Fails when ${target.path} declares a project dependency it is not allowed to name"
            moduleLabel.set(target.path)
            buildFile.set(target.layout.projectDirectory.file("build.gradle.kts"))
            permittedPatterns.set(
                target.provider {
                    rule.permittedProjectDependencyPatterns +
                        extension.additionalPermittedProjectDependencies.get()
                },
            )
            reportFile.set(
                target.layout.buildDirectory.file("reports/plugout/project-dependency-boundary.txt"),
            )
        }

        target.tasks
            .matching { it.name == "preBuild" || it.name == "check" }
            .configureEach {
                dependsOn(boundaryCheck)
                dependsOn(projectDependencyCheck)
            }
    }

    private fun boundaryRuleFor(segments: List<String>): BoundaryRule? {
        val group = segments.firstOrNull() ?: return null
        val name = segments.getOrNull(1) ?: return null
        return when (group) {
            CORE_GROUP -> coreRule(name)
            FEATURE_GROUP -> featureRule(name)
            else -> null
        }
    }

    private fun coreRule(name: String): BoundaryRule =
        if (name in ALWAYS_PRESENT_CORE_MODULES) {
            BoundaryRule(
                guardedPrefixes = listOf(CORE_PREFIX, FEATURE_PREFIX),
                permittedPatterns =
                    (ALWAYS_PRESENT_CORE_MODULES + name)
                        .distinct()
                        .map { module -> "core.$module." },
                permittedProjectDependencyPatterns =
                    (ALWAYS_PRESENT_CORE_MODULES + name)
                        .distinct()
                        .map { module -> ":core:$module" },
                adviceLines =
                    listOf(
                        "core:$name survives every plug-out combination, so it may name only the",
                        "other modules that survive with it: $ALWAYS_PRESENT_DESCRIPTION.",
                        "",
                        "An import or build dependency on an optional module from here makes",
                        "that module undeletable everywhere. Invert the dependency with a",
                        "multibinding, or move the shared type into core:common.",
                    ),
            )
        } else {
            BoundaryRule(
                guardedPrefixes = listOf(FEATURE_PREFIX),
                permittedPatterns = emptyList(),
                permittedProjectDependencyPatterns = listOf(":core:*") ,
                adviceLines =
                    listOf(
                        "core:$name is optional, so it may name any core module - but never a",
                        "feature. A core module that imports a feature inverts the layering and",
                        "makes the feature undeletable. Features reach core through DI, not the",
                        "other way around.",
                    ),
            )
        }

    private fun featureRule(name: String): BoundaryRule =
        BoundaryRule(
            guardedPrefixes = listOf(FEATURE_PREFIX),
            permittedPatterns = listOf("feature.$name.", FEATURE_NAVIGATION_PATTERN),
            permittedProjectDependencyPatterns =
                listOf(
                    ":core:*",
                    ":feature:$name:*",
                    ":feature:*:navigation",
                ),
            adviceLines =
                listOf(
                    "feature:$name may name its own sub-modules and the navigation module of any",
                    "other feature - a route contract is what a feature publishes for others to",
                    "link to. Everything else inside another feature is private.",
                    "",
                    "Need behaviour rather than a route? Publish it through a core contract and",
                    "let both features depend on that instead of on each other.",
                ),
        )

    private data class BoundaryRule(
        val guardedPrefixes: List<String>,
        val permittedPatterns: List<String>,
        val permittedProjectDependencyPatterns: List<String>,
        val adviceLines: List<String>,
    )

    private companion object {
        const val CORE_GROUP = "core"
        const val FEATURE_GROUP = "feature"
        const val CORE_PREFIX = "core."
        const val FEATURE_PREFIX = "feature."
        const val FEATURE_NAVIGATION_PATTERN = "feature.*.navigation."

        val ALWAYS_PRESENT_CORE_MODULES = listOf("common", "navigation", "ui", "data")
        val ALWAYS_PRESENT_DESCRIPTION =
            ALWAYS_PRESENT_CORE_MODULES.joinToString(separator = ", ") { module -> "core:$module" }
    }
}
