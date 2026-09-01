package com.ytapps.composetemplate.convention

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

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
                CheckAppModuleBoundaryTask::class.java,
            )

        boundaryCheck.configure {
            group = "verification"
            description = "Fails when :app imports a symbol from a module that can be plugged out"
            sources.from(target.layout.projectDirectory.dir("src"))
            permittedCoreModules.set(PERMITTED_CORE_MODULES)
            reportFile.set(
                target.layout.buildDirectory.file("reports/plugout/app-module-boundary.txt"),
            )
            // The namespace is assigned by the module's own build script, which runs after this
            // plugin is applied. Reading it through a provider defers the lookup until the task
            // input is resolved, so no afterEvaluate hook is needed.
            applicationPackage.set(
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
        /** Modules that survive every plug-out combination, so `:app` is allowed to name them. */
        val PERMITTED_CORE_MODULES = listOf("common", "navigation", "ui")
    }
}
