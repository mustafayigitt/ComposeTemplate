package com.ytapps.composetemplate.convention

import org.gradle.api.GradleException
import java.io.File

internal enum class DataStrategy(
    val cliValue: String,
    val label: String,
    val usesNetwork: Boolean,
    val usesDatabase: Boolean,
) {
    REMOTE("remote", "Remote only", usesNetwork = true, usesDatabase = false),
    OFFLINE_FIRST("offline-first", "Offline-first", usesNetwork = true, usesDatabase = true),
    LOCAL("local", "Local only", usesNetwork = false, usesDatabase = true),
    MINIMAL("minimal", "No predefined data infrastructure", usesNetwork = false, usesDatabase = false),
    ;

    companion object {
        fun parse(rawValue: String?): DataStrategy {
            val value = rawValue?.trim()?.lowercase() ?: REMOTE.cliValue
            return entries.firstOrNull { it.cliValue == value }
                ?: throw GradleException(
                    "-PdataStrategy must be one of ${entries.joinToString { it.cliValue }}, but was '$value'.",
                )
        }
    }
}

internal object DataStrategyProjector {
    fun project(
        targetDir: File,
        strategy: DataStrategy,
        includeSecrets: Boolean,
    ) {
        if (!includeSecrets) removeSecretInfrastructure(targetDir)

        if (strategy.usesNetwork) {
            applyAuthFlow(targetDir)
        } else {
            removeNetworkCapability(targetDir)
        }
        if (!strategy.usesDatabase) removeDatabaseCapability(targetDir)

        configureFeatureDataConvention(targetDir, strategy)
        writeConsumerDocumentation(targetDir, strategy, includeSecrets)
        removeProjectionInfrastructure(targetDir)
        validate(targetDir, strategy)
    }

    private fun applyAuthFlow(targetDir: File) {
        targetDir.findRequired("feature/splash/domain", "SplashDestination.kt")
            .replaceRequired(
                "    data object Onboarding : SplashDestination\n\n    data object Home : SplashDestination",
                "    data object Onboarding : SplashDestination\n\n    data object Login : SplashDestination\n\n    data object Home : SplashDestination",
            )
        targetDir.findRequired("feature/splash/domain", "ISplashRepository.kt")
            .replaceRequired(
                "interface ISplashRepository {\n    suspend fun isOnboardingCompleted(): Boolean",
                "interface ISplashRepository {\n    suspend fun hasUser(): Boolean\n\n    suspend fun isOnboardingCompleted(): Boolean",
            )
        targetDir.findRequired("feature/splash/data", "SplashRepository.kt")
            .replaceRequired(
                "    ) : ISplashRepository {\n        override suspend fun isOnboardingCompleted(): Boolean",
                "    ) : ISplashRepository {\n        override suspend fun hasUser(): Boolean = preferencesManager.hasUser()\n\n        override suspend fun isOnboardingCompleted(): Boolean",
            )
        targetDir.findRequired("feature/splash/domain", "GetStartDestinationUseCase.kt")
            .replaceRequired(
                """        suspend operator fun invoke(): SplashDestination =
            if (splashRepository.isOnboardingCompleted()) {
                SplashDestination.Home
            } else {
                SplashDestination.Onboarding
            }
""",
                """        suspend operator fun invoke(): SplashDestination {
            if (!splashRepository.isOnboardingCompleted()) {
                return SplashDestination.Onboarding
            }
            return if (splashRepository.hasUser()) SplashDestination.Home else SplashDestination.Login
        }
""",
            )

        File(targetDir, "feature/splash/presentation/build.gradle.kts")
            .replaceRequired(
                "    implementation(project(\":feature:home:navigation\"))\n",
                "    implementation(project(\":feature:home:navigation\"))\n    implementation(project(\":feature:auth:navigation\"))\n",
            )
        targetDir.findRequired("feature/splash/presentation", "SplashViewModel.kt")
            .addAuthImportBeforeHomeImport()
            .replaceRequired(
                "                        SplashDestination.Home -> HomeRoute\n",
                "                        SplashDestination.Home -> HomeRoute\n                        SplashDestination.Login -> LoginRoute\n",
            )
        targetDir.findRequired("feature/splash/domain", "GetStartDestinationUseCaseTest.kt")
            .replaceRequired(
                "            coEvery { splashRepository.isOnboardingCompleted() } returns true\n\n            val result",
                "            coEvery { splashRepository.isOnboardingCompleted() } returns true\n            coEvery { splashRepository.hasUser() } returns true\n\n            val result",
            )

        File(targetDir, "feature/onboarding/presentation/build.gradle.kts")
            .replaceRequired(":feature:home:navigation", ":feature:auth:navigation")
        targetDir.findRequired("feature/onboarding/presentation", "OnboardingRoute.kt")
            .replaceRequired("feature.home.navigation.HomeRoute", "feature.auth.navigation.LoginRoute")
            .replaceRequired("NavigateToHome", "NavigateToLogin")
            .replaceRequired("route = HomeRoute", "route = LoginRoute")
        targetDir.findRequired("feature/onboarding/presentation", "OnboardingViewModel.kt")
            .replaceRequired("NavigateToHome", "NavigateToLogin")

        File(targetDir, "feature/profile/presentation/build.gradle.kts")
            .replaceRequired(":feature:home:navigation", ":feature:auth:navigation")
        targetDir.findRequired("feature/profile/presentation", "ProfileEvent.kt")
            .replaceRequired("NavigateToHome", "NavigateToLogin")
        targetDir.findRequired("feature/profile/presentation", "ProfileRoute.kt")
            .replaceRequired("feature.home.navigation.HomeRoute", "feature.auth.navigation.LoginRoute")
            .replaceRequired("NavigateToHome", "NavigateToLogin")
            .replaceRequired("navigateToTop(HomeRoute)", "navigateToTop(LoginRoute)")
        targetDir.findRequired("feature/profile/presentation", "ProfileViewModel.kt")
            .replaceRequired("NavigateToHome", "NavigateToLogin")
        targetDir.findRequired("feature/profile/presentation", "ProfileViewModelTest.kt")
            .replaceRequired("navigate to home", "navigate to login")
            .replaceRequired("NavigateToHome", "NavigateToLogin")
    }

    private fun removeNetworkCapability(targetDir: File) {
        listOf("core/network", "feature/auth").forEach { File(targetDir, it).deleteRecursively() }
        File(targetDir, "gradle/libs.versions.toml").removeLinesMatching { line ->
            val value = line.trimStart()
            value == "# Network" ||
                value.startsWith("retrofit =") ||
                value.startsWith("converter-gson =") ||
                value.startsWith("okhttp =") ||
                value.startsWith("logging-interceptor =") ||
                value.startsWith("coil-network-okhttp =")
        }
        File(targetDir, "core/ui/build.gradle.kts")
            .removeLinesMatching { it.contains("libs.coil.network.okhttp") }

        val proguard = File(targetDir, "app/proguard-rules.pro")
        if (proguard.exists()) {
            var content = proguard.readText()
            val gsonStart = content.indexOf("# Gson: Keep all network model / DTO classes")
            val serializationStart = content.indexOf("# Kotlinx Serialization:")
            if (gsonStart >= 0 && serializationStart > gsonStart) {
                content = content.removeRange(content.lastIndexOf("# ---", gsonStart), serializationStart)
            }
            val retrofitStart = content.indexOf("# Retrofit: Keep service interface method signatures")
            if (retrofitStart >= 0) {
                content = content.substring(0, content.lastIndexOf("# ---", retrofitStart)).trimEnd() + "\n"
            }
            proguard.writeText(content)
        }
    }

    private fun removeDatabaseCapability(targetDir: File) {
        File(targetDir, "core/database").deleteRecursively()
        targetDir.findByName("AndroidRoomConventionPlugin.kt")?.delete()
        removePluginRegistration(File(targetDir, "build-logic/convention/build.gradle.kts"), "androidRoom")
        File(targetDir, "gradle/libs.versions.toml").removeLinesMatching {
            it.trimStart().startsWith("androidx-room")
        }
        removeDatabaseScaffolding(targetDir.findRequired("build-logic/convention", "ScaffoldFeaturePlugin.kt"))
    }

    private fun removeSecretInfrastructure(targetDir: File) {
        listOf(
            "core/" + "secrets",
            "core/" + "security",
            "secrets" + ".properties.example",
        ).forEach { File(targetDir, it).deleteRecursively() }
        targetDir.findByName("AndroidLibraryNativeConventionPlugin.kt")?.delete()
        targetDir.findByName("Validate" + "SecretsPlugin.kt")?.delete()

        val conventionBuild = File(targetDir, "build-logic/convention/build.gradle.kts")
        removePluginRegistration(conventionBuild, "androidLibrary" + "Native")
        removePluginRegistration(conventionBuild, "validate" + "Secrets")
        File(targetDir, "build.gradle.kts").removeLinesMatching { it.contains(".validate." + "secrets") }

        val projectExtensions = targetDir.findRequired("build-logic/convention", "ProjectExtensions.kt")
        val extensionContent =
            projectExtensions.readText()
                .replace("import java.util.Properties\n", "")
                .substringBefore("\nval Project." + "secrets")
                .trimEnd() + "\n"
        projectExtensions.writeText(extensionContent)

        val appBuild = File(targetDir, "app/build.gradle.kts")
        var appContent =
            appBuild.readLines()
                .filterNot { it.startsWith("import ") && it.endsWith(".convention." + "secrets") }
                .filterNot { it == "import java.util.Properties" }
                .joinToString("\n")
        val signingStart = appContent.indexOf("    val localProperties =")
        val buildTypesStart = appContent.indexOf("    buildTypes {", signingStart)
        if (signingStart >= 0 && buildTypesStart > signingStart) {
            appContent = appContent.removeRange(signingStart, buildTypesStart)
        }
        appContent = appContent.lineSequence()
            .filterNot { it.contains("signingConfig = signingConfigs.getByName(\"release\")") }
            .joinToString("\n").trimEnd() + "\n"
        appBuild.writeText(appContent)

        val gradleProperties = File(targetDir, "gradle.properties")
        if (gradleProperties.exists()) {
            gradleProperties.writeText(
                gradleProperties.readText().substringBefore("\n# Secret Management").trimEnd() + "\n",
            )
        }
        File(targetDir, "gradle/libs.versions.toml").removeLinesMatching { it.trimStart().startsWith("ndk =") }
        File(targetDir, ".gitignore").removeLinesMatching { it.contains("secrets" + ".properties") }
        removeWorkflowStep(targetDir, "Create local.properties and " + "secrets.properties")
    }

    private fun configureFeatureDataConvention(targetDir: File, strategy: DataStrategy) {
        val projects = buildList {
            if (strategy.usesNetwork) add("\":core:network\"")
            if (strategy.usesDatabase) add("\":core:database\"")
        }
        val replacement =
            if (projects.isEmpty()) {
                "            val optionalInfrastructure = emptyList<Project>()"
            } else {
                """            val optionalInfrastructure =
                listOf(${projects.joinToString()})
                    .mapNotNull(rootProject::findProject)"""
            }
        targetDir.findRequired("build-logic/convention", "FeatureDataConventionPlugin.kt")
            .replaceRequired(
                """            val optionalInfrastructure =
                listOf(":core:network", ":core:database")
                    .mapNotNull(rootProject::findProject)""",
                replacement,
            )
    }

    private fun removeDatabaseScaffolding(file: File) {
        removeKotlinBlock(file, "if (module == \"data\" && withDatabase)")
        removeKotlinBlock(file, "private fun getEntityContent(")
        removeKotlinBlock(file, "private fun getDaoContent(")
        val cleaned = file.readLines().filterNot { line ->
            line.contains("val withDatabase =") ||
                line.contains("withDatabase: Boolean,") ||
                line.contains("Room starter files:") ||
                line.contains("val roomPlugin = if (withDatabase)") ||
                line.trim() == "\"\"\" else \"\"" ||
                line.contains("\${roomPlugin.trimEnd()}")
        }.joinToString("\n")
            .replace(
                "getBuildGradleContent(featureName, module, pkg, withDatabase)",
                "getBuildGradleContent(featureName, module, pkg)",
            )
        file.writeText(cleaned.replace(Regex("\n{3,}"), "\n\n").trimEnd() + "\n")
    }

    private fun writeConsumerDocumentation(
        targetDir: File,
        strategy: DataStrategy,
        includeSecrets: Boolean,
    ) {
        val setup = if (includeSecrets) {
            """1. Copy the example secrets file to the local secrets file and replace its placeholders.
2. Run the secrets validation task.
3. Open the project in Android Studio and sync Gradle.
4. Run `./gradlew assembleDebug`."""
        } else {
            """1. Open the project in Android Studio and sync Gradle.
2. Run `./gradlew assembleDebug`."""
        }
        val infrastructure = buildList {
            if (strategy.usesNetwork) add("remote transport and the authentication sample")
            if (strategy.usesDatabase) add("Room persistence")
        }.ifEmpty { listOf("no predefined remote or database implementation") }.joinToString(" and ")
        val databaseScaffold =
            if (strategy.usesDatabase) {
                "\nFor a Room starter entity and DAO, add `-PwithDatabase=true`."
            } else {
                ""
            }

        File(targetDir, "README.md").writeText(
            """# ${targetDir.name}

A modular Android application with a fixed four-module feature vertical.

## Generated configuration

- Data strategy: `${strategy.cliValue}` — ${strategy.label}
- Included infrastructure: $infrastructure
- Secrets and native hardening: ${if (includeSecrets) "included" else "omitted"}

Network-backed strategies route completed onboarding through Login when no stored user exists. Local and minimal strategies route directly to Home and contain no auth feature.

## Architecture

Every retained feature has `data`, `domain`, `navigation` and `presentation` modules.

## Local setup

$setup

## Add a feature

```bash
./gradlew scaffoldFeature -PfeatureName=settings
```
$databaseScaffold

## Verification

```bash
./gradlew ktlintCheck detekt testDebugUnitTest assembleDebug
```
""".trimIndent() + "\n",
        )

        val databaseLine =
            if (strategy.usesDatabase) " Room/KSP tooling is included for database-backed features." else ""
        File(targetDir, "build-logic/README.md").writeText(
            """# Build Logic

Convention plugins keep Android defaults, Compose, Hilt, testing, static analysis, feature layers and module-boundary checks consistent.$databaseLine

`scaffoldFeature` creates the fixed four-module feature vertical. The project generator used to create this application is intentionally not included.

All retained dependencies and versions are centralized in `gradle/libs.versions.toml`.
""".trimIndent() + "\n",
        )
    }

    private fun removeProjectionInfrastructure(targetDir: File) {
        targetDir.findByName("DataStrategyProjectionPlugin.kt")?.delete()
        targetDir.findByName("DataStrategyProjector.kt")?.delete()
        removePluginRegistration(
            File(targetDir, "build-logic/convention/build.gradle.kts"),
            "dataStrategyProjection",
        )
        File(targetDir, "app/build.gradle.kts")
            .removeLinesMatching { it.contains(".data.strategy.projection") }
    }

    private fun validate(targetDir: File, strategy: DataStrategy) {
        val forbiddenPaths = buildList {
            if (!strategy.usesNetwork) addAll(listOf("core/network", "feature/auth"))
            if (!strategy.usesDatabase) add("core/database")
        }
        val pathViolations = forbiddenPaths.filter { File(targetDir, it).exists() }
        val forbiddenTokens = buildList {
            if (!strategy.usesNetwork) {
                addAll(listOf("core:network", "core.network", "feature:auth", "feature.auth", "libs.retrofit", "libs.okhttp", "libs.converter.gson", "libs.logging.interceptor", "libs.coil.network.okhttp", "AppNoInternetBanner", "NetworkMonitor"))
            }
            if (!strategy.usesDatabase) {
                addAll(listOf("core:database", "core.database", "androidx-room", "androidx.room", "AndroidRoomConventionPlugin", ".android.room", "withDatabase"))
            }
            add("data.strategy.projection")
            add("DataStrategyProjector")
        }
        val contentViolations = mutableListOf<String>()
        targetDir.walkTopDown().filter { it.isFile && it.extension in TEXT_EXTENSIONS }.forEach { file ->
            val content = file.readText()
            forbiddenTokens.filter(content::contains).forEach { token ->
                contentViolations += "${file.relativeTo(targetDir)} contains '$token'"
            }
        }
        if (pathViolations.isNotEmpty() || contentViolations.isNotEmpty()) {
            val details = buildList {
                pathViolations.forEach { add("forbidden path exists: $it") }
                addAll(contentViolations)
            }.joinToString("\n - ")
            throw GradleException("Generated ${strategy.cliValue} project contains data-strategy residue:\n - $details")
        }
    }

    private fun File.addAuthImportBeforeHomeImport(): File {
        val content = readText()
        val homeImport = content.lineSequence().firstOrNull { it.endsWith("feature.home.navigation.HomeRoute") }
            ?: throw GradleException("Could not find HomeRoute import in $path")
        val authImport = homeImport.replace("feature.home.navigation.HomeRoute", "feature.auth.navigation.LoginRoute")
        return replaceRequired(homeImport, "$authImport\n$homeImport")
    }

    private fun File.findRequired(modulePath: String, fileName: String): File =
        File(this, modulePath).walkTopDown().singleOrNull { it.isFile && it.name == fileName }
            ?: throw GradleException("Could not find $fileName under $modulePath")

    private fun File.findByName(fileName: String): File? =
        walkTopDown().firstOrNull { it.isFile && it.name == fileName }

    private fun File.replaceRequired(oldValue: String, newValue: String): File {
        val content = readText()
        if (!content.contains(oldValue)) throw GradleException("Expected text not found in $path")
        writeText(content.replace(oldValue, newValue))
        return this
    }

    private fun File.removeLinesMatching(predicate: (String) -> Boolean) {
        if (exists()) writeText(readLines().filterNot(predicate).joinToString("\n").trimEnd() + "\n")
    }

    private fun removeKotlinBlock(file: File, startToken: String) {
        val result = mutableListOf<String>()
        var skipping = false
        var sawOpeningBrace = false
        var braceCount = 0
        file.readLines().forEach { line ->
            if (!skipping && line.contains(startToken)) skipping = true
            if (skipping) {
                val opens = line.count { it == '{' }
                val closes = line.count { it == '}' }
                if (opens > 0) sawOpeningBrace = true
                braceCount += opens - closes
                if (sawOpeningBrace && braceCount == 0) {
                    skipping = false
                    sawOpeningBrace = false
                }
            } else {
                result += line
            }
        }
        file.writeText(result.joinToString("\n").trimEnd() + "\n")
    }

    private fun removePluginRegistration(file: File, registrationName: String) {
        if (!file.exists()) return
        val result = mutableListOf<String>()
        var skipping = false
        var braceCount = 0
        file.readLines().forEach { line ->
            if (!skipping && line.contains("register(\"$registrationName\")")) skipping = true
            if (skipping) {
                braceCount += line.count { it == '{' }
                braceCount -= line.count { it == '}' }
                if (braceCount == 0 && line.contains("}")) skipping = false
            } else {
                result += line
            }
        }
        file.writeText(result.joinToString("\n").replace(Regex("\n{3,}"), "\n\n").trimEnd() + "\n")
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

    private val TEXT_EXTENSIONS = setOf("kt", "kts", "xml", "properties", "pro", "txt", "md", "yml", "yaml", "json", "toml")
}
