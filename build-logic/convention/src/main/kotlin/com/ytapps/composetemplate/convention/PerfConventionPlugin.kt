package com.ytapps.composetemplate.convention

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Wires baseline profile generation into the application module, but only when the generator
 * project is part of the build.
 *
 * The conditional is the entire point. `:app` used to apply `androidx.baselineprofile` and
 * declare `baselineProfile(project(":baselineprofile"))` plus the `profileinstaller` runtime
 * dependency itself, which meant deleting `baselineprofile/` broke configuration of the
 * application module. Performance tooling was therefore the last piece of the template that
 * could not be plugged out, failing the contract's third criterion: a module's Gradle wiring
 * must be self-contained.
 *
 * Since `settings.gradle.kts` discovers modules from the filesystem, "is the generator part of
 * this build" and "does the folder exist" are now the same question, and
 * [Project.findProject] answers it without any flag to keep in sync.
 *
 * The plugin id is applied rather than the typed extension being configured, so
 * `androidx.baselineprofile` does not need to be on the convention project's compile
 * classpath. It reaches the plugin classpath through `apply false` in the root build script,
 * which is the same mechanism [AndroidHiltConventionPlugin] relies on for Hilt.
 *
 * Every lambda handed to Gradle elsewhere in this build is parameterless and uses its receiver,
 * because Gradle's `Action<T>` surfaces in Kotlin as `T.() -> Unit`.
 */
class PerfConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val generator = target.rootProject.findProject(GENERATOR_PROJECT_PATH)
        if (generator == null) {
            target.logger.lifecycle(
                "Baseline profiles are disabled: $GENERATOR_PROJECT_PATH is not part of this build.",
            )
            return
        }

        target.pluginManager.apply(BASELINE_PROFILE_PLUGIN_ID)
        target.dependencies.add(BASELINE_PROFILE_CONFIGURATION, generator)
        target.dependencies.add(
            "implementation",
            target.libs.findLibrary("androidx-profileinstaller").get(),
        )
    }

    private companion object {
        const val GENERATOR_PROJECT_PATH = ":baselineprofile"
        const val BASELINE_PROFILE_PLUGIN_ID = "androidx.baselineprofile"
        const val BASELINE_PROFILE_CONFIGURATION = "baselineProfile"
    }
}
