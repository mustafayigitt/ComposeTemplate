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
     *
     * ```
     * moduleBoundary {
     *     additionalPermittedImports.add("feature.auth.domain.")
     * }
     * ```
     */
    abstract val additionalPermittedImports: ListProperty<String>
}

/**
 * Applies the plug-out boundary rule to every core and feature module.
 *
 * `composetemplate.app.boundary` proved the rule can be executable rather than a review
 * convention, but it only covered `:app`. Every other edge stayed unguarded: a feature could
 * import another feature's internals, and a core module that is never deleted could import one
 * that is, which quietly makes the optional module undeletable everywhere.
 *
 * The rule is derived from the module's own Gradle path rather than configured, so a module
 * added later is governed the moment it exists on disk - the same property that made module
 * discovery worth doing.
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
            // Both the namespace and the module's own exceptions are set by the build script
            // that applies this plugin, so they are read through providers rather than now.
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

        target.tasks
            .matching { it.name == "preBuild" || it.name == "check" }
            .configureEach { dependsOn(boundaryCheck) }
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

    /**
     * A core module never imports a feature. Beyond that the rule splits in two, because the
     * damage an import does depends on whether this module can itself be deleted.
     */
    private fun coreRule(name: String): BoundaryRule =
        if (name in ALWAYS_PRESENT_CORE_MODULES) {
            BoundaryRule(
                guardedPrefixes = listOf(CORE_PREFIX, FEATURE_PREFIX),
                permittedPatterns =
                    (ALWAYS_PRESENT_CORE_MODULES + name)
                        .distinct()
                        .map { module -> "core.$module." },
                adviceLines =
                    listOf(
                        "core:$name survives every plug-out combination, so it may name only the",
                        "other modules that survive with it: $ALWAYS_PRESENT_DESCRIPTION.",
                        "",
                        "An import of an optional module from here makes that module undeletable",
                        "everywhere, because this module is never the one being deleted.",
                        "Invert the dependency with a multibinding, or move the shared type into",
                        "core:common.",
                    ),
            )
        } else {
            BoundaryRule(
                guardedPrefixes = listOf(FEATURE_PREFIX),
                permittedPatterns = emptyList(),
                adviceLines =
                    listOf(
                        "core:$name is optional, so it may name any core module - but never a",
                        "feature. A core module that imports a feature inverts the layering and",
                        "makes the feature undeletable. Features reach core through DI, not the",
                        "other way around.",
                    ),
            )
        }

    /**
     * A feature may name its own sub-modules and any other feature's navigation module. The
     * navigation module is what a feature publishes: `feature:auth:presentation` links to a
     * route owned by `feature:splash:navigation` today, and that edge is intended. Its data and
     * presentation code are private, because coupling to those is what stops two features from
     * being removed independently.
     */
    private fun featureRule(name: String): BoundaryRule =
        BoundaryRule(
            guardedPrefixes = listOf(FEATURE_PREFIX),
            permittedPatterns = listOf("feature.$name.", FEATURE_NAVIGATION_PATTERN),
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
        val adviceLines: List<String>,
    )

    private companion object {
        const val CORE_GROUP = "core"
        const val FEATURE_GROUP = "feature"
        const val CORE_PREFIX = "core."
        const val FEATURE_PREFIX = "feature."

        /** Any feature's navigation module, whatever the feature is called. */
        const val FEATURE_NAVIGATION_PATTERN = "feature.*.navigation."

        /**
         * Core modules that no plug-out combination removes. They are the only modules another
         * always-present module is allowed to name.
         */
        val ALWAYS_PRESENT_CORE_MODULES = listOf("common", "navigation", "ui", "data")

        val ALWAYS_PRESENT_DESCRIPTION =
            ALWAYS_PRESENT_CORE_MODULES.joinToString(separator = ", ") { module -> "core:$module" }
    }
}
