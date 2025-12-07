pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
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
include(":core")
include(":contract")
include(":feature:auth:data")
include(":feature:auth:domain")
include(":feature:auth:presentation")
include(":feature:detail:data")
include(":feature:detail:domain")
include(":feature:detail:presentation")
include(":feature:list:data")
include(":feature:list:domain")
include(":feature:list:presentation")
include(":feature:profile:data")
include(":feature:profile:domain")
include(":feature:profile:presentation")
include(":feature:search:data")
include(":feature:search:domain")
include(":feature:search:presentation")
include(":feature:splash:data")
include(":feature:splash:domain")
include(":feature:splash:presentation")
include(":feature:home:data")
include(":feature:home:domain")
include(":feature:home:presentation")
