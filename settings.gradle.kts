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
include(":app")
include(":core:common")
include(":core:secrets")
include(":core:data")
include(":core:network")
include(":core:security")
include(":core:ui")
include(":core:navigation")
include(":core:analytics")
include(":core:config")
include(":core:permission")
include(":core:google-play")
include(":core:database")
include(":feature:auth:data")
include(":feature:auth:domain")
include(":feature:auth:navigation")
include(":feature:auth:presentation")
include(":feature:detail:data")
include(":feature:detail:domain")
include(":feature:detail:navigation")
include(":feature:detail:presentation")
include(":feature:list:data")
include(":feature:list:domain")
include(":feature:list:navigation")
include(":feature:list:presentation")
include(":feature:profile:data")
include(":feature:profile:domain")
include(":feature:profile:navigation")
include(":feature:profile:presentation")
include(":feature:onboarding:data")
include(":feature:onboarding:domain")
include(":feature:onboarding:navigation")
include(":feature:onboarding:presentation")
include(":feature:search:data")
include(":feature:search:domain")
include(":feature:search:navigation")
include(":feature:search:presentation")
include(":feature:splash:data")
include(":feature:splash:domain")
include(":feature:splash:navigation")
include(":feature:splash:presentation")
include(":feature:home:data")
include(":feature:home:domain")
include(":feature:home:navigation")
include(":feature:home:presentation")
include(":benchmark")
include(":baselineprofile")
