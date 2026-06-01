pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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

rootProject.name = "AuraLife"
include(":app")
include(":domain")
include(":core:common")
include(":core:network")
include(":core:firebase")
include(":core:database")
include(":core:navigation")
include(":core:designsystem")
include(":data")
include(":feature:splash")
include(":feature:onboarding")
include(":feature:auth")
include(":feature:home")
include(":feature:explore")
include(":feature:film")
include(":feature:library")
include(":feature:history")
include(":feature:search")
include(":feature:payment")
