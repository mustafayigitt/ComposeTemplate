package com.ytapps.composetemplate.convention

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Enforces plug-out criterion (2): the application module must never name a symbol that
 * belongs to a module which is allowed to be deleted.
 *
 * Dependency injection is only half of the contract. Kotlin resolves imports before Dagger
 * runs, so a single `import` from `:app` into an optional module turns "delete the folder"
 * into a compile error that no binding can rescue. This plugin fails the build at that
 * import instead of waiting for the CI plug-out job to discover it.
 *
 * The rule is an allowlist rather than a blocklist: `:app` may only reach the core modules
 * that survive every plug-out combination. A module introduced later is therefore forbidden
 * by default, without anyone having to remember to extend a list.
 */
class AppModuleBoundaryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // Registered with the two-argument overload on purpose. The three-argument form
        // competes with register(String, Class, Object... constructorArgs), and Kotlin binds a
        // trailing lambda to the vararg candidate, leaving the lambda parameter untyped.
        val boundaryCheck =
            target.tasks.register(
                "checkAppModuleBoundary",
                CheckAppModuleBoundaryTask::class.java,
            )

        boundaryCheck.configure { task: CheckAppModuleBoundaryTask ->
            task.group = "verification"
            task.description = "Fails when :app imports a symbol from a module that can be plugged out"
            task.sources.from(target.layout.projectDirectory.dir("src"))
            task.permittedCoreModules.set(PERMITTED_CORE_MODULES)
            task.applicationPackage.convention("")
            task.reportFile.set(
                target.layout.buildDirectory.file("reports/plugout/app-module-boundary.txt"),
            )
        }

        // The namespace is assigned inside the module's own build script, which runs after
        // this plugin is applied, so it can only be read once evaluation has finished.
        target.afterEvaluate { project: Project ->
            val namespace = project.extensions.findByType(ApplicationExtension::class.java)?.namespace
            boundaryCheck.configure { task: CheckAppModuleBoundaryTask ->
                task.applicationPackage.set(namespace.orEmpty())
            }
        }

        target.tasks
            .matching { task: Task -> task.name == "preBuild" || task.name == "check" }
            .configureEach { task: Task -> task.dependsOn(boundaryCheck) }
    }

    private companion object {
        /** Modules that survive every plug-out combination, so `:app` is allowed to name them. */
        val PERMITTED_CORE_MODULES = listOf("common", "navigation", "ui")
    }
}
