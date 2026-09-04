plugins {
    `kotlin-dsl`
}

group = "com.ytapps.composetemplate.convention"

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
    compileOnly(libs.compose.compiler.gradle.plugin)
    compileOnly(libs.detekt.gradle.plugin)
    compileOnly(libs.ktlint.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("createNewApp") {
            id = "composetemplate.create.new.app"
            implementationClass = "com.ytapps.composetemplate.convention.CreateNewAppPlugin"
        }
        register("androidApplication") {
            id = "composetemplate.android.application"
            implementationClass = "com.ytapps.composetemplate.convention.AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "composetemplate.android.application.compose"
            implementationClass = "com.ytapps.composetemplate.convention.AndroidComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "composetemplate.android.library"
            implementationClass = "com.ytapps.composetemplate.convention.AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "composetemplate.android.library.compose"
            implementationClass = "com.ytapps.composetemplate.convention.AndroidComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "composetemplate.android.hilt"
            implementationClass = "com.ytapps.composetemplate.convention.AndroidHiltConventionPlugin"
        }
        register("androidRoom") {
            id = "composetemplate.android.room"
            implementationClass = "com.ytapps.composetemplate.convention.AndroidRoomConventionPlugin"
        }
        register("test") {
            id = "composetemplate.test"
            implementationClass = "com.ytapps.composetemplate.convention.TestConventionPlugin"
        }
        register("featureDomain") {
            id = "composetemplate.feature.domain"
            implementationClass = "com.ytapps.composetemplate.convention.FeatureDomainConventionPlugin"
        }
        register("featureData") {
            id = "composetemplate.feature.data"
            implementationClass = "com.ytapps.composetemplate.convention.FeatureDataConventionPlugin"
        }
        register("featureNavigation") {
            id = "composetemplate.feature.navigation"
            implementationClass = "com.ytapps.composetemplate.convention.FeatureNavigationConventionPlugin"
        }
        register("featurePresentation") {
            id = "composetemplate.feature.presentation"
            implementationClass = "com.ytapps.composetemplate.convention.FeaturePresentationConventionPlugin"
        }
        register("staticAnalysis") {
            id = "composetemplate.static.analysis"
            implementationClass = "com.ytapps.composetemplate.convention.StaticAnalysisConventionPlugin"
        }
        register("androidLibraryNative") {
            id = "composetemplate.android.library.native"
            implementationClass = "com.ytapps.composetemplate.convention.AndroidLibraryNativeConventionPlugin"
        }
        register("validateSecrets") {
            id = "composetemplate.validate.secrets"
            implementationClass = "com.ytapps.composetemplate.convention.ValidateSecretsPlugin"
        }
        register("scaffoldFeature") {
            id = "composetemplate.scaffold.feature"
            implementationClass = "com.ytapps.composetemplate.convention.ScaffoldFeaturePlugin"
        }
        register("baselineProfileGenerator") {
            id = "composetemplate.baseline.profile.generator"
            implementationClass = "com.ytapps.composetemplate.convention.BaselineProfileGeneratorConventionPlugin"
        }
        register("appModuleBoundary") {
            id = "composetemplate.app.boundary"
            implementationClass = "com.ytapps.composetemplate.convention.AppModuleBoundaryPlugin"
        }
        register("moduleBoundary") {
            id = "composetemplate.module.boundary"
            implementationClass = "com.ytapps.composetemplate.convention.ModuleBoundaryPlugin"
        }
        register("perf") {
            id = "composetemplate.perf"
            implementationClass = "com.ytapps.composetemplate.convention.PerfConventionPlugin"
        }
    }
}
