package com.ytapps.composetemplate.convention

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class ScaffoldFeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.register("scaffoldFeature") {
            group = "setup"
            description = "Scaffolds a new feature module with data, domain, navigation, presentation sub-modules"

            doLast {
                val featureName = target.findProperty("featureName")?.toString()
                    ?: throw IllegalArgumentException("Required: -PfeatureName=<name> (e.g., -PfeatureName=settings)")
                if (!FEATURE_NAME_REGEX.matches(featureName)) {
                    throw GradleException("featureName must be lower_snake_case, for example: settings or user_profile")
                }

                val pkg = target.findProperty("featurePkg")?.toString()
                    ?: "com.ytapps.composetemplate.feature"
                val withDatabase = target.findProperty("withDatabase")?.toString()?.toBooleanStrictOrNull() ?: false

                val featureDir = File(target.rootProject.rootDir, "feature/$featureName")
                if (featureDir.exists()) {
                    throw GradleException("Feature '$featureName' already exists at ${featureDir.absolutePath}")
                }

                featureDir.mkdirs()

                val subModules = listOf("domain", "data", "navigation", "presentation")
                val featurePkg = "$pkg.$featureName"
                val routeName = featureName.toPascalCase()

                subModules.forEach { module ->
                    val moduleDir = File(featureDir, module)
                    moduleDir.mkdirs()

                    val srcDir = File(moduleDir, "src/main/java")
                    val basePath = featurePkg.replace('.', File.separatorChar)
                    File(srcDir, basePath).mkdirs()

                    val buildGradle = getBuildGradleContent(featureName, module, pkg, withDatabase)
                    File(moduleDir, "build.gradle.kts").writeText(buildGradle)

                    if (module == "navigation") {
                        val navigationDir = File(srcDir, "$basePath/navigation")
                        navigationDir.mkdirs()
                        val routeFile = File(navigationDir, "${routeName}Route.kt")
                        routeFile.writeText(getNavigationContent(featureName, featurePkg))
                    }
                    if (module == "domain") {
                        val domainDir = File(srcDir, "$basePath/domain")
                        domainDir.mkdirs()
                        File(domainDir, "Get${routeName}TitleUseCase.kt").writeText(getDomainContent(featureName, featurePkg))
                    }

                    if (module == "presentation") {
                        val presentationDir = File(srcDir, "$basePath/presentation")
                        val diDir = File(presentationDir, "di")
                        presentationDir.mkdirs()
                        diDir.mkdirs()

                        File(presentationDir, "${routeName}UiState.kt").writeText(getUiStateContent(routeName, featurePkg))
                        File(presentationDir, "${routeName}Event.kt").writeText(getEventContent(routeName, featurePkg))
                        File(presentationDir, "${routeName}ViewModel.kt").writeText(getViewModelContent(routeName, featurePkg))
                        File(presentationDir, "${routeName}Route.kt").writeText(getPresentationRouteContent(featureName, routeName, featurePkg))
                        File(presentationDir, "${routeName}ScreenProvider.kt").writeText(getScreenProviderContent(routeName, featurePkg))
                        File(diDir, "${routeName}Module.kt").writeText(getPresentationModuleContent(routeName, featurePkg))

                        val resDir = File(moduleDir, "src/main/res")
                        val valuesDir = File(resDir, "values")
                        val valuesTrDir = File(resDir, "values-tr")
                        valuesDir.mkdirs()
                        valuesTrDir.mkdirs()

                        File(valuesDir, "strings.xml").writeText(getStringsContent(featureName, routeName))
                        File(valuesTrDir, "strings.xml").writeText(getStringsContent(featureName, routeName))
                    }

                    if (module == "data" && withDatabase) {
                        val daoDir = File(srcDir, "$basePath/data/dao")
                        val entityDir = File(srcDir, "$basePath/data/entity")
                        daoDir.mkdirs()
                        entityDir.mkdirs()
                        File(entityDir, "${routeName}Entity.kt").writeText(getEntityContent(featureName, routeName, featurePkg))
                        File(daoDir, "${routeName}Dao.kt").writeText(getDaoContent(featureName, routeName, featurePkg))
                    }
                }

                // settings.gradle.kts and app/build.gradle.kts are intentionally left alone. Both
                // derive their module list from the folders that exist on disk, so creating the
                // directories above is already enough to register the feature with the build.

                logger.lifecycle(
                    """
                    |
                    |✅ Feature '$featureName' scaffolded at feature/$featureName/
                    |
                    |Automation:
                    |  - settings.gradle.kts: no edit needed, modules are discovered from disk
                    |  - app/build.gradle.kts: no edit needed, :app wires every discovered module
                    |  - Room starter files: ${if (withDatabase) "created" else "skipped"}
                    |
                    |Next steps:
                    |  1. Replace generated title/loading UI with real screen state.
                    |  2. Add repository/use case contracts if the feature needs data.
                    |  3. Run ./gradlew :feature:$featureName:presentation:compileDebugKotlin.
                    |
                """.trimMargin()
                )
            }
        }
    }

    private fun getBuildGradleContent(
        featureName: String,
        module: String,
        pkg: String,
        withDatabase: Boolean,
    ): String {
        val namespace = "$pkg.$featureName.$module"
        return when (module) {
            "domain" -> """plugins {
    id("composetemplate.feature.domain")
}

android {
    namespace = "$namespace"
}
"""
            "data" -> {
                val roomPlugin = if (withDatabase) """    id("composetemplate.android.room")
""" else ""
                """plugins {
    id("composetemplate.feature.data")
${roomPlugin.trimEnd()}
}

android {
    namespace = "$namespace"
}

dependencies {
    implementation(project(":feature:$featureName:domain"))
}
"""
            }
            "navigation" -> """plugins {
    id("composetemplate.feature.navigation")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "$namespace"
}
"""
            "presentation" -> """plugins {
    id("composetemplate.feature.presentation")
}

android {
    namespace = "$namespace"
}

dependencies {
    implementation(project(":feature:$featureName:domain"))
    implementation(project(":feature:$featureName:navigation"))
}
"""
            else -> ""
        }
    }

    private fun getNavigationContent(featureName: String, pkg: String): String {
        val routeName = featureName.toPascalCase()
        return """package $pkg.navigation

import com.ytapps.composetemplate.core.navigation.INavigationItem
import kotlinx.serialization.Serializable

@Serializable
data object ${routeName}Route : INavigationItem {
    override val route: String = "route_$featureName"
}
"""
    }

    private fun getDomainContent(featureName: String, featurePkg: String): String {
        val routeName = featureName.toPascalCase()
        return """package $featurePkg.domain

import javax.inject.Inject

class Get${routeName}TitleUseCase
    @Inject
    constructor() {
        operator fun invoke(): String = "$routeName"
    }
"""
    }

    private fun getEntityContent(featureName: String, routeName: String, featurePkg: String): String {
        return """package $featurePkg.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "$featureName")
data class ${routeName}Entity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
)
"""
    }

    private fun getDaoContent(featureName: String, routeName: String, featurePkg: String): String {
        return """package $featurePkg.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import $featurePkg.data.entity.${routeName}Entity
import kotlinx.coroutines.flow.Flow

@Dao
interface ${routeName}Dao {
    @Query("SELECT * FROM $featureName")
    fun observeAll(): Flow<List<${routeName}Entity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: ${routeName}Entity)

    @Delete
    suspend fun delete(entity: ${routeName}Entity)

    @Query("DELETE FROM $featureName")
    suspend fun clear()
}
"""
    }

    private fun getUiStateContent(routeName: String, featurePkg: String): String {
        return """package $featurePkg.presentation

data class ${routeName}UiState(
    val isLoading: Boolean = false,
)
"""
    }

    private fun getEventContent(routeName: String, featurePkg: String): String {
        return """package $featurePkg.presentation

sealed interface ${routeName}Event
"""
    }

    private fun getViewModelContent(routeName: String, featurePkg: String): String {
        return """package $featurePkg.presentation

import androidx.lifecycle.viewModelScope
import com.ytapps.composetemplate.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ${routeName}ViewModel
    @Inject
    constructor() : BaseViewModel<${routeName}UiState, ${routeName}Event>() {
        override val uiStateInternal = MutableStateFlow(${routeName}UiState())

        init {
            viewModelScope.launch {
                updateState { it.copy(isLoading = false) }
            }
        }
    }
"""
    }

    private fun getPresentationRouteContent(featureName: String, routeName: String, featurePkg: String): String {
        return """package $featurePkg.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import $featurePkg.presentation.R

@Composable
fun ${routeName}Route(
    viewModel: ${routeName}ViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ${routeName}Screen(
        title = stringResource(R.string.feature_${featureName}_title),
        uiState = uiState,
    )
}

@Composable
internal fun ${routeName}Screen(
    title: String,
    uiState: ${routeName}UiState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Text(text = title)
        }
    }
}
"""
    }

    private fun getScreenProviderContent(routeName: String, featurePkg: String): String {
        return """package $featurePkg.presentation

import androidx.compose.runtime.Composable
import com.ytapps.composetemplate.core.navigation.INavigationItem
import com.ytapps.composetemplate.core.navigation.INavigationManager
import com.ytapps.composetemplate.core.navigation.IScreenProvider
import $featurePkg.navigation.${routeName}Route
import javax.inject.Inject

class ${routeName}ScreenProvider
    @Inject
    constructor() : IScreenProvider {
        @Composable
        override fun provideScreen(
            route: INavigationItem,
            navigationManager: INavigationManager,
        ): Boolean =
            when (route) {
                is ${routeName}Route -> {
                    ${routeName}Route()
                    true
                }

                else -> false
            }
    }
"""
    }

    private fun getPresentationModuleContent(routeName: String, featurePkg: String): String {
        return """package $featurePkg.presentation.di

import com.ytapps.composetemplate.core.navigation.IScreenProvider
import $featurePkg.presentation.${routeName}ScreenProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ${routeName}Module {
    @Binds
    @IntoSet
    abstract fun bind${routeName}ScreenProvider(provider: ${routeName}ScreenProvider): IScreenProvider
}
"""
    }

    private fun getStringsContent(featureName: String, routeName: String): String {
        return """<resources>
    <string name="feature_${featureName}_title">$routeName</string>
</resources>
"""
    }

    private fun String.toPascalCase(): String =
        split('-', '_')
            .filter { it.isNotBlank() }
            .joinToString("") { part -> part.replaceFirstChar { it.uppercase() } }

    companion object {
        val FEATURE_NAME_REGEX = Regex("^[a-z][a-z0-9_]*$")
    }
}
