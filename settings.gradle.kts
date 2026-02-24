pluginManagement {
    repositories {
        google {content {
            includeGroupByRegex("com\\.android.*")
            includeGroupByRegex("com\\.google.*")
            includeGroupByRegex("androidx.*")
        }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

// Change "dependencyResolution" to "dependencyResolutionManagement"
dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "QuizApp"
include(":app")