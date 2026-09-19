package com.ytapps.composetemplate.convention

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
                cleanupNewProject(targetDir, finalAppName)

                printFinalSummary(targetDir.name)
            }
        }
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

    private fun cleanupNewProject(targetDir: File, appName: String) {
        targetDir.walkTopDown().forEach { if (it.name == "CreateNewAppPlugin.kt") it.delete() }
        val conventionBuild = File(targetDir, "build-logic/convention/build.gradle.kts")
        if (conventionBuild.exists()) {
            val lines = conventionBuild.readLines()
            val result = mutableListOf<String>()
            var skipBlock = false
            var braceCount = 0

            for (line in lines) {
                if (line.contains("register(\"createNewApp\")")) {
                    skipBlock = true
                }

                if (skipBlock) {
                    braceCount += line.count { it == '{' }
                    braceCount -= line.count { it == '}' }
                    if (braceCount == 0 && line.contains("}")) {
                        skipBlock = false
                    }
                    continue
                }
                result.add(line)
            }
            conventionBuild.writeText(result.joinToString("\n").replace(Regex("\n{3,}"), "\n\n"))
        }

        targetDir.walkTopDown().forEach { file ->
            if (file.name == "build.gradle.kts") {
                val lines = file.readLines()
                val cleanedLines = lines.filterNot { it.contains(Regex("""id\(".*\.create\.new\.app"\)""")) }
                file.writeText(cleanedLines.joinToString("\n"))
            }
        }

        removeTemplateOnlyWorkflowJobs(targetDir)
        writeConsumerReadme(targetDir, appName)
        writeConsumerBuildLogicReadme(targetDir)
        validateGeneratedProject(targetDir)
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

    private fun writeConsumerReadme(targetDir: File, appName: String) {
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

1. Copy `secrets.properties.example` to `secrets.properties` and replace the placeholders.
2. Run `./gradlew validateSecrets`.
3. Open the project in Android Studio and sync Gradle.
4. Run `./gradlew assembleDebug`.

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

    private fun validateGeneratedProject(targetDir: File) {
        val forbiddenPaths = listOf(
            "wiki",
            "mkdocs.yml",
            "CONTRIBUTING.md",
            ".github/workflows/pages.yml",
            "build-logic/convention/src/main/kotlin/com/ytapps/composetemplate/convention/CreateNewAppPlugin.kt",
        )
        val missingPaths = forbiddenPaths.filter { File(targetDir, it).exists() }

        val forbiddenTokens = listOf(
            "com.ytapps.composetemplate",
            "composetemplate.create.new.app",
            "CreateNewAppPlugin",
            "create-new-app",
            "mkdocs.yml",
            "https://mustafayigitt.github.io/ComposeTemplate/",
            "mustafayigitt/ComposeTemplate",
        )
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
    private fun printSuccess(msg: String) = println("${green("✓")} $msg")
    private fun blue(t: String) = "\u001B[34m$t\u001B[0m"
    private fun green(t: String) = "\u001B[32m$t\u001B[0m"
    private fun red(t: String) = "\u001B[31m$t\u001B[0m"
    private fun yellow(t: String) = "\u001B[33m$t\u001B[0m"
}
