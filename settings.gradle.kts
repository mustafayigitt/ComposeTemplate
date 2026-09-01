import java.io.File

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ComposeTemplate"

/*
 * Modules are discovered from the filesystem instead of being listed one by one.
 *
 * The plug-out contract says a module must be removable by deleting its folder. That was never
 * quite true while every module also had to be named here: deleting a folder left a dangling
 * include() behind, and a dangling include() does not break one module, it breaks configuration
 * for the entire build. Scanning removes that second step, and it removes the mirror-image step
 * for scaffolding, which no longer has to patch this file to register a generated feature.
 *
 * A directory is a module when it directly contains a build.gradle.kts. Directories are still
 * traversed after being included, because feature modules nest one level deeper than core ones.
 */
val nonModuleDirectories = setOf("build", "build-logic", "buildSrc", "gradle", "src")

fun includeModulesUnder(
    directory: File,
    parentPath: String,
) {
    directory
        .listFiles()
        ?.filter { it.isDirectory && !it.name.startsWith(".") && it.name !in nonModuleDirectories }
        ?.sortedBy { it.name }
        ?.forEach { candidate ->
            val modulePath = "$parentPath:${candidate.name}"
            if (File(candidate, "build.gradle.kts").isFile) {
                include(modulePath)
            }
            includeModulesUnder(candidate, modulePath)
        }
}

includeModulesUnder(rootDir, "")
