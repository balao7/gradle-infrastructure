package com.redmadrobot.build

import com.android.build.gradle.BaseExtension
import com.redmadrobot.build.extension.AndroidOptions
import com.redmadrobot.build.internal.android
import com.redmadrobot.build.internal.configureKotlin
import com.redmadrobot.build.internal.setTestOptions
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.android
import org.gradle.kotlin.dsl.repositories
import java.io.File

/**
 * Base plugin with common configurations for both application and library modules.
 * @see AndroidLibraryPlugin
 * @see AndroidApplicationPlugin
 */
public abstract class BaseAndroidPlugin : InfrastructurePlugin() {

    /** Should be called from [configure] in implementation. */
    protected fun Project.applyBaseAndroidPlugin(pluginId: String) {
        apply {
            plugin(pluginId)
            plugin("kotlin-android")

            // Apply fix for Android caching problems
            // See https://github.com/gradle/android-cache-fix-gradle-plugin
            plugin("org.gradle.android.cache-fix")
        }

        val extension = redmadrobotExtension
        configureKotlin(extension.jvmTarget)
        configureAndroid(extension.android, extension.jvmTarget)
        configureRepositories()
    }
}

private fun Project.configureAndroid(
    options: AndroidOptions,
    jvmTarget: Property<JavaVersion>,
) = android<BaseExtension> {
    compileSdkVersion(options.compileSdk.get())
    options.buildToolsVersion.orNull?.let(::buildToolsVersion)

    defaultConfig {
        minSdkVersion(options.minSdk.get())
        targetSdkVersion(options.targetSdk.get())
    }

    // Set NDK version from env variable if exists
    val requestedNdkVersion = System.getenv("ANDROID_NDK_VERSION")
    if (requestedNdkVersion != null) ndkVersion = requestedNdkVersion

    compileOptions {
        sourceCompatibility = jvmTarget.get()
        targetCompatibility = jvmTarget.get()
    }

    @Suppress("UnstableApiUsage")
    with(buildFeatures) {
        aidl = false
        renderScript = false
        shaders = false
    }

    afterEvaluate {
        // Add kotlin folder to all source sets
        // Do it after evaluate because there can be added build types
        for (sourceSet in sourceSets) {
            val javaSrcDirs = sourceSet.java.srcDirs.map(File::toString)
            val kotlinSrcDirs = javaSrcDirs.map { it.replace("/java", "/kotlin") }
            sourceSet.java.srcDirs(javaSrcDirs + kotlinSrcDirs)
        }

        // Keep only release unit tests to reduce tests execution time
        tasks.named("test") {
            setDependsOn(dependsOn.filter { it !is TaskProvider<*> || it.name.endsWith("ReleaseUnitTest") })
        }
    }

    testOptions {
        unitTests.all { it.setTestOptions(options.test) }
    }
}

private fun Project.configureRepositories() {
    repositories {
        mavenCentral()
        google()
    }
}
