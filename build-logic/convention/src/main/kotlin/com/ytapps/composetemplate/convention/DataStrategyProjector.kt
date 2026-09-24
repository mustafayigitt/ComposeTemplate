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
    fun apply(
        targetDir: File,
        strategy: DataStrategy,
    ) {
        if (strategy.usesNetwork) {
            applyAuthFlow(targetDir)
        } else {
            removeNetworkCapability(targetDir)
        }

        if (!strategy.usesDatabase) {
            removeDatabaseCapability(targetDir)
        }

        configureFeatureDataConvention(targetDir, strategy)
    }

    fun validate(
        targetDir: File,
        strategy: DataStrategy,
    ) {
        val forbiddenPaths = buildList {
            if (!strategy.usesNetwork) {
                add("core/network")
                add("feature/auth")
            }
            if (!strategy.usesDatabase) {
                add("core/database")
                add(
                    "build-logic/convention/src/main/kotlin/com/ytapps/composetemplate/convention/AndroidRoomConventionPlugin.kt",
                )
            }
        }
        val pathViolations = forbiddenPaths.filter { File(targetDir, it).exists() }

        val forbiddenTokens = buildList {
            if (!strategy.usesNetwork) {
                addAll(
                    listOf(
                        "core:network",
                        "core.network",
                        "feature:auth",
                        "feature.auth",
                        "libs.retrofit",
                        "libs.okhttp",
                        "libs.converter.gson",
                        "libs.logging.interceptor",
                        "libs.coil.network.okhttp",
                        "AppNoInternetBanner",
                        "NetworkMonitor",
                    ),
                )
            }
            if (!strategy.usesDatabase) {
                addAll(
                    listOf(
                        "core:database",
                        "core.database",
                        "androidx-room",
                        "androidx.room",
                        "AndroidRoomConventionPlugin",
                        "composetemplate.android.room",
                        "withDatabase",
                    ),
                )
            }
        }
        val contentViolations = mutableListOf<String>()
        targetDir.walkTopDown()
            .filter { it.isFile && it.extension in TEXT_EXTENSIONS }
            .forEach { file ->
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

    private fun applyAuthFlow(targetDir: File) {
        file(targetDir, "feature/splash/domain/src/main/java/com/ytapps/composetemplate/feature/splash/domain/SplashDestination.kt")
            .replaceRequired(
                "    data object Onboarding : SplashDestination\n\n    data object Home : SplashDestination",
                "    data object Onboarding : SplashDestination\n\n    data object Login : SplashDestination\n\n    data object Home : SplashDestination",
            )

        file(targetDir, "feature/splash/domain/src/main/java/com/ytapps/composetemplate/feature/splash/domain/ISplashRepository.kt")
            .replaceRequired(
                "interface ISplashRepository {\n    suspend fun isOnboardingCompleted(): Boolean",
                "interface ISplashRepository {\n    suspend fun hasUser(): Boolean\n\n    suspend fun isOnboardingCompleted(): Boolean",
            )

        file(targetDir, "feature/splash/data/src/main/java/com/ytapps/composetemplate/feature/splash/data/SplashRepository.kt")
            .replaceRequired(
                "    ) : ISplashRepository {\n        override suspend fun isOnboardingCompleted(): Boolean",
                "    ) : ISplashRepository {\n        override suspend fun hasUser(): Boolean = preferencesManager.hasUser()\n\n        override suspend fun isOnboardingCompleted(): Boolean",
            )

        file(targetDir, "feature/splash/domain/src/main/java/com/ytapps/composetemplate/feature/splash/domain/GetStartDestinationUseCase.kt")
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

        file(targetDir, "feature/splash/presentation/build.gradle.kts")
            .replaceRequired(
                "    implementation(project(\":feature:home:navigation\"))\n",
                "    implementation(project(\":feature:home:navigation\"))\n    implementation(project(\":feature:auth:navigation\"))\n",
            )
        file(targetDir, "feature/splash/presentation/src/main/java/com/ytapps/composetemplate/feature/splash/presentation/SplashViewModel.kt")
            .replaceRequired(
                "import com.ytapps.composetemplate.feature.home.navigation.HomeRoute",
                "import com.ytapps.composetemplate.feature.auth.navigation.LoginRoute\nimport com.ytapps.composetemplate.feature.home.navigation.HomeRoute",
            ).replaceRequired(
                "                        SplashDestination.Home -> HomeRoute\n",
                "                        SplashDestination.Home -> HomeRoute\n                        SplashDestination.Login -> LoginRoute\n",
            )

        val useCaseTest =
            file(
                targetDir,
                "feature/splash/domain/src/test/java/com/ytapps/composetemplate/feature/splash/domain/GetStartDestinationUseCaseTest.kt",
            )
        useCaseTest.replaceRequired(
            "            coEvery { splashRepository.isOnboardingCompleted() } returns true\n\n            val result",
            "            coEvery { splashRepository.isOnboardingCompleted() } returns true\n            coEvery { splashRepository.hasUser() } returns true\n\n            val result",
        )

        file(targetDir, "feature/onboarding/presentation/build.gradle.kts")
            .replaceRequired(":feature:home:navigation", ":feature:auth:navigation")
        file(targetDir, "feature/onboarding/presentation/src/main/java/com/ytapps/composetemplate/feature/onboarding/presentation/OnboardingRoute.kt")
            .replaceRequired(
                "com.ytapps.composetemplate.feature.home.navigation.HomeRoute",
                "com.ytapps.composetemplate.feature.auth.navigation.LoginRoute",
            ).replaceRequired("NavigateToHome", "NavigateToLogin")
            .replaceRequired("route = HomeRoute", "route = LoginRoute")
        file(targetDir, "feature/onboarding/presentation/src/main/java/com/ytapps/composetemplate/feature/onboarding/presentation/OnboardingViewModel.kt")
            .replaceRequired("NavigateToHome", "NavigateToLogin")

        file(targetDir, "feature/profile/presentation/build.gradle.kts")
            .replaceRequired(":feature:home:navigation", ":feature:auth:navigation")
        file(targetDir, "feature/profile/presentation/src/main/java/com/ytapps/composetemplate/feature/profile/presentation/ProfileEvent.kt")
            .replaceRequired("NavigateToHome", "NavigateToLogin")
        file(targetDir, "feature/profile/presentation/src/main/java/com/ytapps/composetemplate/feature/profile/presentation/ProfileRoute.kt")
            .replaceRequired(
                "com.ytapps.composetemplate.feature.home.navigation.HomeRoute",
                "com.ytapps.composetemplate.feature.auth.navigation.LoginRoute",
            ).replaceRequired("NavigateToHome", "NavigateToLogin")
            .replaceRequired("navigateToTop(HomeRoute)", "navigateToTop(LoginRoute)")
        file(targetDir, "feature/profile/presentation/src/main/java/com/ytapps/composetemplate/feature/profile/presentation/ProfileViewModel.kt")
            .replaceRequired("NavigateToHome", "NavigateToLogin")
        file(targetDir, "feature/profile/presentation/src/test/java/com/ytapps/composetemplate/feature/profile/presentation/ProfileViewModelTest.kt")
            .replaceRequired("navigate to home", "navigate to login")
            .replaceRequired("NavigateToHome", "NavigateToLogin")
    }

    private fun removeNetworkCapability(targetDir: File) {
        listOf("core/network", "feature/auth").forEach { File(targetDir, it).deleteRecursively() }

        File(targetDir, "gradle/libs.versions.toml").removeLinesMatching { line ->
            val value = line.trimStart()
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
                val sectionStart = content.lastIndexOf("# -----------------------------------------------------------", gsonStart)
                content = content.removeRange(sectionStart, serializationStart)
            }
            val retrofitStart = content.indexOf("# Retrofit: Keep service interface method signatures")
            if (retrofitStart >= 0) {
                val sectionStart = content.lastIndexOf("# -----------------------------------------------------------", retrofitStart)
                content = content.substring(0, sectionStart).trimEnd() + "\n"
            }
            proguard.writeText(content)
        }
    }

    private fun removeDatabaseCapability(targetDir: File) {
        File(targetDir, "core/database").deleteRecursively()
        File(
            targetDir,
            "build-logic/convention/src/main/kotlin/com/ytapps/composetemplate/convention/AndroidRoomConventionPlugin.kt",
        ).delete()
        removePluginRegistration(
            File(targetDir, "build-logic/convention/build.gradle.kts"),
            "androidRoom",
        )
        File(targetDir, "gradle/libs.versions.toml").removeLinesMatching { line ->
            line.trimStart().startsWith("androidx-room")
        }
        removeDatabaseScaffolding(
            File(
                targetDir,
                "build-logic/convention/src/main/kotlin/com/ytapps/composetemplate/convention/ScaffoldFeaturePlugin.kt",
            ),
        )
    }

    private fun configureFeatureDataConvention(
        targetDir: File,
        strategy: DataStrategy,
    ) {
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
        file(
            targetDir,
            "build-logic/convention/src/main/kotlin/com/ytapps/composetemplate/convention/FeatureDataConventionPlugin.kt",
        ).replaceRequired(
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

        val cleaned =
            file.readLines().filterNot { line ->
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

    private fun removeKotlinBlock(
        file: File,
        startToken: String,
    ) {
        if (!file.exists()) return
        val result = mutableListOf<String>()
        var skipping = false
        var sawOpeningBrace = false
        var braceCount = 0

        file.readLines().forEach { line ->
            if (!skipping && line.contains(startToken)) {
                skipping = true
            }
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

    private fun removePluginRegistration(
        file: File,
        registrationName: String,
    ) {
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
                if (braceCount == 0 && line.contains("}")) skipping = false
            } else {
                result += line
            }
        }
        file.writeText(result.joinToString("\n").replace(Regex("\n{3,}"), "\n\n").trimEnd() + "\n")
    }

    private fun file(
        root: File,
        path: String,
    ): File = File(root, path)

    private fun File.replaceRequired(
        oldValue: String,
        newValue: String,
    ): File {
        val content = readText()
        if (!content.contains(oldValue)) {
            throw GradleException("Could not project data strategy: expected text not found in ${path}.")
        }
        writeText(content.replace(oldValue, newValue))
        return this
    }

    private fun File.removeLinesMatching(predicate: (String) -> Boolean) {
        if (!exists()) return
        writeText(readLines().filterNot(predicate).joinToString("\n").trimEnd() + "\n")
    }

    private val TEXT_EXTENSIONS =
        setOf("kt", "kts", "xml", "properties", "pro", "txt", "md", "yml", "yaml", "json", "toml")
}
