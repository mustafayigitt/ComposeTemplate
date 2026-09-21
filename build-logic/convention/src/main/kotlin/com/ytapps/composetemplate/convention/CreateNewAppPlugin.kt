package com.ytapps.composetemplate.convention

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.util.Scanner

class CreateNewAppPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.tasks.register("create-new-app") {
            group = "setup"
            description = "Creates a new Android project with ComposeTemplate"

            doLast {
                val scanner = Scanner(System.`in`)
                val rawArgs = target.findProperty("args")?.toString()?.split(",") ?: emptyList()
                val withSecrets = target.booleanProperty("withSecrets", defaultValue = true)

                val finalAppId: String
                val finalAppName: String

                if (rawArgs.size >= 2) {
                    finalAppId = rawArgs[0].trim().lowercase()
                    finalAppName = rawArgs[1].trim()
                } else {
                    println("\n${yellow("? Enter the New Application Name (e.g., MyAwesomeApp):")}")
                    finalAppName = scanner.nextLine().trim()

                    println(yellow("? Enter the New Application ID (e.g., com.example.app):"))
                    finalAppId = scanner.nextLine().trim().lowercase()
                }

                if (!validateInputs(finalAppId, finalAppName)) return@doLast

                val targetDir = File(target.rootDir.parentFile, finalAppName)
                if (targetDir.exists()) {
                    println("❌ ${red("Error:")} Target directory '${targetDir.name}' already exists.")
                    return@doLast
                }

                printHeader()

                printStep("Copying consumer project files...")
                target.copy {
                    from(target.rootDir)
                    into(targetDir)
                    exclude(
                        ".git",
                        ".gradle",
                        ".idea",
                        ".kotlin",
                        ".agents",
                        ".codex",
                        ".artifacts",
                        ".navigation",
                        ".cxx",
                        ".externalNativeBuild",
                        "**/build",
                        "local.properties",
                        "secrets.properties",
                        "**/.DS_Store",
                        "**/*.apk",
                        "**/*.aab",
                        "**/*.ap_",
                        "**/*.dex",
                        "**/*.keystore",
                        "**/*.hprof",
                        "**/*.log",
                        "wiki",
                        "wiki/**",
                        "mkdocs.yml",
                        "CONTRIBUTING.md",
                        ".github/workflows/pages.yml",
                    )
                }

                printStep("Updating package names and references...")
                updateContent(targetDir, finalAppId, finalAppName)

                printStep("Restructuring directory hierarchy...")
                refactorDirectories(targetDir, "com.ytapps.composetemplate", finalAppId)

                printStep("Removing template-only setup and documentation...")
                cleanupNewProject(targetDir, finalAppName, withSecrets)

                printFinalSummary(targetDir.name)
            }
        }
    }

    private fun Project.booleanProperty(name: String, defaultValue: Boolean): Boolean {
        val value = findProperty(name)?.toString() ?: return defaultValue
        return value.toBooleanStrictOrNull()
            ?: throw GradleException("-$name must be either true or false, but was '$value'.")
    }

    private fun validateInputs(appId: String, appName: String): Boolean {
        val idRegex = Regex("^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)+$")
        val nameRegex = Regex("^[a-zA-Z][a-zA-Z0-9]*$")
        return appId.matches(idRegex) && appName.matches(nameRegex)
    }

    private fun updateContent(targetDir: File, appId: String, appName: String) {
        val oldPackage = "com.ytapps.composetemplate"
        val oldPrefix = "composetemplate"
        val newPrefix = appId.substringAfterLast(".")

        targetDir.walkTopDown().forEach { file ->
            if (file.isFile && isTextFile(file)) {
                val content = file.readText()
                val updated = content
                    .replace(oldPackage, appId)
                    .replace("ComposeTemplate", appName)
                    .replace("$oldPrefix.", "$newPrefix.")

                if (content != updated) file.writeText(updated)
            }
        }
    }

    private fun isTextFile(file: File) = file.extension in listOf(
        "kt",
        "kts",
        "xml",
        "properties",
        "pro",
        "txt",
        "md",
        "yml",
        "yaml",
        "json",
        "toml",
    )

    private fun refactorDirectories(targetDir: File, oldPkg: String, newPkg: String) {
        val oldPath = oldPkg.replace(".", File.separator)
        val newPath = newPkg.replace(".", File.separator)

        val sourceRoots = mutableListOf<String>()
        targetDir.walkTopDown().filter { it.isDirectory && (it.name == "java" || it.name == "kotlin") }.forEach { dir ->
            if (dir.absolutePath.contains("src${File.separator}main") ||
                dir.absolutePath.contains("src${File.separator}test") ||
                dir.absolutePath.contains("src${File.separator}androidTest")
            ) {
                sourceRoots.add(dir.absolutePath.removePrefix(targetDir.absolutePath).removePrefix(File.separator))
            }
        }

        sourceRoots.forEach { path ->
            val root = File(targetDir, path)
            val oldFolder = File(root, oldPath)
            if (oldFolder.exists()) {
                val newFolder = File(root, newPath).apply { mkdirs() }
                oldFolder.copyRecursively(newFolder, overwrite = true)
                oldFolder.deleteRecursively()

                // Clean up parent folders if they are empty or contain only the path to our project
                var parent = oldFolder.parentFile
                while (parent != null && parent != root) {
                    if (parent.listFiles()?.isEmpty() == true) {
                        parent.delete()
                        parent = parent.parentFile
                    } else {
                        break
                    }
                }
            }
        }
    }

    private fun cleanupNewProject(targetDir: File, appName: String, withSecrets: Boolean) {
        if (!withSecrets) {
            printStep("Removing unselected secrets and hardening infrastructure...")
            removeSecretsCapability(targetDir)
        }

        targetDir.walkTopDown().forEach { if (it.name == "CreateNewAppPlugin.kt") it.delete() }
        removePluginRegistration(
            File(targetDir, "build-logic/convention/build.gradle.kts"),
            "createNewApp",
        )

        targetDir.walkTopDown().forEach { file ->
            if (file.name == "build.gradle.kts") {
                val lines = file.readLines()
                val cleanedLines = lines.filterNot { it.contains(Regex("""id\(".*\.create\.new\.app"\)""")) }
                file.writeText(cleanedLines.joinToString("\n"))
            }
        }

        removeTemplateOnlyWorkflowJobs(targetDir)
        writeConsumerReadme(targetDir, appName, withSecrets)
        writeConsumerBuildLogicReadme(targetDir)
        validateGeneratedProject(targetDir, withSecrets)
    }

    private fun removeSecretsCapability(targetDir: File) {
        listOf(
            "core/secrets",
            "core/security",
            "secrets.properties.example",
            "build-logic/convention/src/main/kotlin/com/ytapps/composetemplate/convention/AndroidLibraryNativeConventionPlugin.kt",
            "build-logic/convention/src/main/kotlin/com/ytapps/composetemplate/convention/ValidateSecretsPlugin.kt",
        ).forEach { path -> File(targetDir, path).deleteRecursively() }

        val conventionBuild = File(targetDir, "build-logic/convention/build.gradle.kts")
        removePluginRegistration(conventionBuild, "androidLibraryNative")
        removePluginRegistration(conventionBuild, "validateSecrets")

        val rootBuild = File(targetDir, "build.gradle.kts")
        rootBuild.removeLinesContaining(".validate.secrets")

        val projectExtensions =
            File(
                targetDir,
                "build-logic/convention/src/main/kotlin/com/ytapps/composetemplate/convention/ProjectExtensions.kt",
            )
        if (projectExtensions.exists()) {
            val content =
                projectExtensions
                    .readText()
                    .replace("import java.util.Properties\n", "")
                    .substringBefore("\nval Project.secrets")
                    .trimEnd() + "\n"
            projectExtensions.writeText(content)
        }

        val appBuild = File(targetDir, "app/build.gradle.kts")
        if (appBuild.exists()) {
            var content =
                appBuild
                    .readText()
                    .replace(Regex("import .*\.convention\.secrets\n"), "")
                    .replace("import java.util.Properties\n", "")
            val signingStart = content.indexOf("    val localProperties =")
            val buildTypesStart = content.indexOf("    buildTypes {", signingStart)
            if (signingStart >= 0 && buildTypesStart > signingStart) {
                content = content.removeRange(signingStart, buildTypesStart)
            }
            content =
                content
                    .lineSequence()
                    .filterNot { it.contains("signingConfig = signingConfigs.getByName(\"release\")") }
                    .joinToString("\n")
                    .trimEnd() + "\n"
            appBuild.writeText(content)
        }

        val gradleProperties = File(targetDir, "gradle.properties")
        if (gradleProperties.exists()) {
            gradleProperties.writeText(
                gradleProperties
                    .readText()
                    .substringBefore("\n# Secret Management")
                    .trimEnd() + "\n",
            )
        }

        File(targetDir, ".gitignore").removeLinesContaining("secrets.properties")
        removeWorkflowStep(targetDir, "Create local.properties and secrets.properties")
    }

    private fun removePluginRegistration(file: File, registrationName: String) {
        if (!file.exists()) return

        val result = mutableListOf<String>()
        var skipping = false
        var braceCount = 0

        file.readLines().forEach { line ->
            if (!skipping && line.contains("register(\"$registrationName\")")) {
                skipping = true
            }

            if (skipping) {
                braceCount += line.count { it == '{' }
                braceCount -= line.count { it == '}' }
                if (braceCount == 0 && line.contains("}")) {
                    skipping = false
                }
            } else {
                result += line
            }
        }

        file.writeText(result.joinToString("\n").replace(Regex("\n{3,}"), "\n\n").trimEnd() + "\n")
    }

    private fun File.removeLinesContaining(token: String) {
        if (!exists()) return
        writeText(readLines().filterNot { it.contains(token) }.joinToString("\n").trimEnd() + "\n")
    }

    private fun removeWorkflowStep(targetDir: File, stepName: String) {
        val workflow = File(targetDir, ".github/workflows/ci.yml")
        if (!workflow.exists()) return

        val stepHeader = "      - name: $stepName"
        var skipping = false
        val cleaned = workflow.readLines().filter { line ->
            if (line == stepHeader) {
                skipping = true
                false
            } else if (skipping && line.startsWith("      - ")) {
                skipping = false
                true
            } else {
                !skipping
            }
        }

        workflow.writeText(cleaned.joinToString("\n").replace(Regex("\n{3,}"), "\n\n").trimEnd() + "\n")
    }

    private fun removeTemplateOnlyWorkflowJobs(targetDir: File) {
        val workflow = File(targetDir, ".github/workflows/ci.yml")
        if (!workflow.exists()) return

        val templateOnlyJobs = setOf("plug-out", "template-smoke")
        val jobPattern = Regex("^  ([A-Za-z0-9_-]+):$")
        var skipping = false
        val cleaned = workflow.readLines().filter { line ->
            jobPattern.matchEntire(line)?.groupValues?.get(1)?.let { jobName ->
                skipping = jobName in templateOnlyJobs
            }
            !skipping
        }

        workflow.writeText(cleaned.joinToString("\n").trimEnd() + "\n")
    }

    private fun writeConsumerReadme(targetDir: File, appName: String, withSecrets: Boolean) {
        val localSetup =
            if (withSecrets) {
                """1. Copy `secrets.properties.example` to `secrets.properties` and replace the placeholders.
2. Run `./gradlew validateSecrets`.
3. Open the project in Android Studio and sync Gradle.
4. Run `./gradlew assembleDebug`."""
            } else {
                """1. Open the project in Android Studio and sync Gradle.
2. Run `./gradlew assembleDebug`."""
            }

        File(targetDir, "README.md").writeText(
            """# $appName

A modular Android application with a fixed feature vertical and shared build conventions.

## Architecture

Every feature follows the same four-module structure:

- `data` — data sources, repositories and mappers
- `domain` — contracts, use cases and business rules
- `navigation` — typed routes and navigation contracts
- `presentation` — UI state, events, ViewModels and screens

## Local setup

$localSetup

## Add a feature

```bash
./gradlew scaffoldFeature -PfeatureName=settings
```

The task creates the four feature modules and module discovery registers them automatically.
Replace the generated placeholder code with the feature's real contracts, data flow and UI.

## Verification

```bash
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug
```

The project applies module-boundary checks during normal build and test tasks so feature code
cannot silently depend on layers or modules outside the architecture rules.
""".trimIndent() + "\n",
        )
    }

    private fun writeConsumerBuildLogicReadme(targetDir: File) {
        File(targetDir, "build-logic/README.md").writeText(
            """# Build Logic

This directory contains the convention plugins shared by the application modules.

The convention plugins keep Android defaults, Compose, Hilt, Room, testing, static analysis,
feature layers and module-boundary checks consistent across the project.

`scaffoldFeature` remains available for creating a new four-module feature vertical. The project
generator used to create this application is intentionally not included in the generated project.

All dependencies and versions remain centralized in `gradle/libs.versions.toml`.
""".trimIndent() + "\n",
        )
    }

    private fun validateGeneratedProject(targetDir: File, withSecrets: Boolean) {
        val forbiddenPaths = buildList {
            addAll(
                listOf(
                    "wiki",
                    "mkdocs.yml",
                    "CONTRIBUTING.md",
                    ".github/workflows/pages.yml",
                    "build-logic/convention/src/main/kotlin/com/ytapps/composetemplate/convention/CreateNewAppPlugin.kt",
                ),
            )
            if (!withSecrets) {
                addAll(
                    listOf(
                        "core/secrets",
                        "core/security",
                        "secrets.properties.example",
                        "build-logic/convention/src/main/kotlin/com/ytapps/composetemplate/convention/AndroidLibraryNativeConventionPlugin.kt",
                        "build-logic/convention/src/main/kotlin/com/ytapps/composetemplate/convention/ValidateSecretsPlugin.kt",
                    ),
                )
            }
        }
        val missingPaths = forbiddenPaths.filter { File(targetDir, it).exists() }

        val forbiddenTokens = buildList {
            addAll(
                listOf(
                    "com.ytapps.composetemplate",
                    "composetemplate.create.new.app",
                    "CreateNewAppPlugin",
                    "create-new-app",
                    "mkdocs.yml",
                    "https://mustafayigitt.github.io/ComposeTemplate/",
                    "mustafayigitt/ComposeTemplate",
                ),
            )
            if (!withSecrets) {
                addAll(
                    listOf(
                        "core:secrets",
                        "core.security",
                        "SecretManager",
                        ".android.library.native",
                        ".validate.secrets",
                        "validateSecrets",
                        "scanApkForSecrets",
                        "hardeningReport",
                        "secrets.properties",
                        "useNativeSecrets",
                        "NATIVE_RUNTIME_CHECKS_ENABLED",
                        "EXPECTED_SIGNATURE_HASH",
                        "XOR_MASK",
                    ),
                )
            }
        }
        val contentViolations = mutableListOf<String>()

        targetDir.walkTopDown()
            .filter { it.isFile && isTextFile(it) }
            .forEach { file ->
                val content = file.readText()
                forbiddenTokens.filter(content::contains).forEach { token ->
                    contentViolations += "${file.relativeTo(targetDir)} contains '$token'"
                }
            }

        if (missingPaths.isNotEmpty() || contentViolations.isNotEmpty()) {
            val details = buildList {
                missingPaths.forEach { add("forbidden path exists: $it") }
                addAll(contentViolations)
            }.joinToString("\n - ")
            throw GradleException("Generated project contains template-only residue:\n - $details")
        }
    }

    private fun printHeader() {
        println("\n${blue("╔════════════════════════════════════════════════════════════╗")}")
        println("${blue("║         Project Initialization Started                     ║")}")
        println("${blue("╚════════════════════════════════════════════════════════════╝")}\n")
    }

    private fun printFinalSummary(dirName: String) {
        println("\n${green("✔ Project created successfully!")}")
        println("${yellow("Next steps:")}")
        println("  1. cd ../$dirName")
        println("  2. git init && git add . && git commit -m \"Initial commit\"")
        println("  3. Open with Android Studio & Sync Gradle")
        println("\nHappy Coding! 🚀\n")
    }

    private fun printStep(msg: String) = println("${blue("ℹ")} $msg")
    private fun blue(t: String) = "\u001B[34m$t\u001B[0m"
    private fun green(t: String) = "\u001B[32m$t\u001B[0m"
    private fun red(t: String) = "\u001B[31m$t\u001B[0m"
    private fun yellow(t: String) = "\u001B[33m$t\u001B[0m"
}
