package com.ytapps.composetemplate.convention

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Enforces the plug-out boundary rules for the application module.
 *
 * `:app` has two coupling surfaces: Kotlin imports and literal project dependencies in its
 * build script. The first task protects the source boundary; the second protects the build-file
 * boundary without rejecting the intentional dynamic module discovery expression in
 * `app/build.gradle.kts`.
 *
 * Every lambda below is parameterless on purpose. Gradle's `Action<T>` parameters surface
 * in Kotlin as `T.() -> Unit`, so declaring a parameter is a compile error. `matching` is
 * the exception: it takes a `Spec<T>`, which stays an ordinary single-argument lambda.
 */
class AppModuleBoundaryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension =
            target.extensions.create("moduleBoundary", ModuleBoundaryExtension::class.java)

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
            modulePathSegments.set(emptyList<String>())
            reportFile.set(
                target.layout.buildDirectory.file("reports/plugout/app-module-boundary.txt"),
            )
            moduleNamespace.set(
                target.provider {
                    target.extensions.findByType(ApplicationExtension::class.java)
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
                "Fails when :app declares a project dependency it is not allowed to name"
            moduleLabel.set(":app")
            buildFile.set(target.layout.projectDirectory.file("build.gradle.kts"))
            permittedPatterns.set(
                target.provider {
                    PERMITTED_PROJECT_DEPENDENCY_PATTERNS +
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

    private companion object {
        const val CORE_PREFIX = "core."
        const val FEATURE_PREFIX = "feature."

        val PERMITTED_CORE_MODULES = listOf("common", "navigation", "ui")
        val PERMITTED_PROJECT_DEPENDENCY_PATTERNS =
            PERMITTED_CORE_MODULES.map { module -> ":core:$module" }

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
