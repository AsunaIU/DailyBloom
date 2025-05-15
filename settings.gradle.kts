pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "DailyBloom"
include(":app")
include(":domain")
include(":data")
include(":presentation")
include(":data:mylibrary")
include(":domain:mylibrary")
include(":presentation:mylibrary")
