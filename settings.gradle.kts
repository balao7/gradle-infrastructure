pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "redmadrobot") {
                useModule("com.redmadrobot.build:infrastructure:${requested.version}")
            }
        }
    }
}

rootProject.name = "infrastructure"

include(
    ":infrastructure",
    ":infrastructure-android",
)

enableFeaturePreview("VERSION_CATALOGS")
dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    versionCatalogs {
        create("libs")
    }
}
