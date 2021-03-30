import com.redmadrobot.build.extension.*

plugins {
    `kotlin-dsl`
    id("redmadrobot.publish")
    id("redmadrobot.kotlin-library")
}

group = "com.redmadrobot.build"
description = "Small plugins to reduce boilerplate in Gradle build scripts."
version = "0.8.2"

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

gradlePlugin {
    plugins {
        register("root-project") {
            id = "redmadrobot.root-project"
            implementationClass = "com.redmadrobot.build.RootProjectPlugin"
        }
        register("application") {
            id = "redmadrobot.application"
            implementationClass = "com.redmadrobot.build.AndroidApplicationPlugin"
        }
        register("android-library") {
            id = "redmadrobot.android-library"
            implementationClass = "com.redmadrobot.build.AndroidLibraryPlugin"
        }
        register("kotlin-library") {
            id = "redmadrobot.kotlin-library"
            implementationClass = "com.redmadrobot.build.KotlinLibraryPlugin"
        }
        register("publish") {
            id = "redmadrobot.publish"
            implementationClass = "com.redmadrobot.build.PublishPlugin"
        }
        register("detekt") {
            id = "redmadrobot.detekt"
            implementationClass = "com.redmadrobot.build.DetektPlugin"
        }
    }
}

repositories {
    mavenCentral()
    google()
    jcenter {
        content {
            // TODO #36 Remove this after update to new AGP version
            //  See: https://youtrack.jetbrains.com/issue/IDEA-261387
            includeModule("org.jetbrains.trove4j", "trove4j")
        }
    }
}

dependencies {
    implementation("com.android.tools.build:gradle:4.1.3")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.16.0")
    implementation(kotlin("gradle-plugin", version = "1.4.32"))
}

publishing {
    repositories {
        if (isRunningOnCi) githubPackages("RedMadRobot/gradle-infrastructure")
        if (isReleaseVersion && credentialsExist("ossrh")) ossrh()
    }
}
