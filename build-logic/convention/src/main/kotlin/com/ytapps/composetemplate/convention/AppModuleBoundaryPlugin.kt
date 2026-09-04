package com.ytapps.composetemplate.convention

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Enforces plug-out criterion (2) for the application module: `:app` must never name a symbol
 * that belongs to a module which is allowed to be deleted.
 *
 * Dependency injection is only half of the contract. Kotlin resolves imports before Dagger
 * runs, so a single `import` from `:app` into an optional module turns "delete the folder"
 * into a compile error that no binding can rescue. This plugin fails the build at that
 * import instead of waiting for the CI plug-out job to discover it.
 *
 * The rule is an allowlist rather than a blocklist: `:app` may only reach the core modules
 * that survive every plug-out combination. A module introduced later is therefore forbidden
 * by default, without anyone having to remember to extend a list.
 *
 * The scanning itself lives in [CheckModuleBoundaryTask], which the library modules share
 * through `composetemplate.module.boundary`. Only the rule differs: `:app` sits at the root of
 * the package tree, so it passes no path segments and guards both `core.` and `feature.`.
 *
 * Every lambda below is parameterless on purpose. Gradle's `Action<T>` parameters surface
 * in Kotlin as `T.() -> Unit`, so declaring a parameter is a compile error. `matching` is
 * the exception: it takes a `Spec<T>`, which stays an ordinary single-argument lambda.
 */
class AppModuleBoundaryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val boundaryCheck =
            target.tasks.register(
                "checkAppModuleBoundary",
                CheckModuleBoundaryTask::class.java,
            )

        boundaryCheck.configure {
            group = "verification"
            description = "Fails when :app imports a symbol from a module that can be plugged out"
            moduleLabel.set("The application module")
            sources.from(target.layout.projectDirectory.dir("src"))
            guardedPrefixes.set(listOf(CORE_PREFIX, FEATURE_PREFIX))
            permittedPatterns.set(PERMITTED_CORE_MODULES.map { module -> "core.$module." })
            adviceLines.set(ADVICE_LINES)
            // The application namespace is already the package root, so nothing has to be
            // trimmed off it.
            modulePathSegments.set(emptyList<String>())
            reportFile.set(
                target.layout.buildDirectory.file("reports/plugout/app-module-boundary.txt"),
            )
            // The namespace is assigned by the module's own build script, which runs after this
            // plugin is applied. Reading it through a provider defers the lookup until the task
            // input is resolved, so no afterEvaluate hook is needed.
            moduleNamespace.set(
                target.provider {
                    target.extensions.findByType(ApplicationExtension::class.java)
                        ?.namespace
                        .orEmpty()
                },
            )
        }

        target.tasks
            .matching { it.name == "preBuild" || it.name == "check" }
            .configureEach { dependsOn(boundaryCheck) }
    }

    private companion object {
        const val CORE_PREFIX = "core."
        const val FEATURE_PREFIX = "feature."

        /** Modules that survive every plug-out combination, so `:app` is allowed to name them. */
        val PERMITTED_CORE_MODULES = listOf("common", "navigation", "ui")

        val ADVICE_LINES =
            listOf(
                ":app may import these core modules only: core.common, core.navigation, core.ui",
                "- plus its own packages.",
                "",
                "Optional modules must reach the app through a multibinding, not an import:",
                "  startup work      -> contribute an AppInitializer",
                "  navigation events -> contribute a NavigationObserver",
                "  a misplaced type  -> move it into core:common",
            )
    }
}
