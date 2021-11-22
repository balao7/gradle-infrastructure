description = "Small plugins to reduce boilerplate in Android projects' Gradle build scripts."

gradlePlugin {
    plugins {
        register("android-config") {
            id = "redmadrobot.android-config"
            implementationClass = "com.redmadrobot.build.android.AndroidConfigPlugin"
        }
        register("application") {
            id = "redmadrobot.application"
            implementationClass = "com.redmadrobot.build.android.AndroidApplicationPlugin"
        }
        register("android-library") {
            id = "redmadrobot.android-library"
            implementationClass = "com.redmadrobot.build.android.AndroidLibraryPlugin"
        }
    }
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    api(projects.infrastructureKotlin)
    implementation(libs.androidGradle)
    implementation(libs.androidGradleCacheFix)
}
