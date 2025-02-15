@file:Suppress("UnstableApiUsage") // We want to use new APIs

package com.redmadrobot.build.android

import com.android.build.api.dsl.LibraryExtension
import com.redmadrobot.build.android.internal.android
import com.redmadrobot.build.android.internal.androidFinalizeDsl
import com.redmadrobot.build.android.internal.projectProguardFiles
import com.redmadrobot.build.internal.InternalGradleInfrastructureApi
import com.redmadrobot.build.kotlin.internal.kotlin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

private const val ARG_EXPLICIT_API = "-Xexplicit-api"

/**
 * Plugin that applies default configurations for Android library project.
 * Should be applied in place of `com.android.library`.
 *
 * Tied to `com.redmadrobot.android-library` plugin ID.
 */
public class AndroidLibraryPlugin : BaseAndroidPlugin() {

    @InternalGradleInfrastructureApi
    override fun Project.configure() {
        applyBaseAndroidPlugin("com.android.library")
        val androidOptions = configPlugin.androidOptions

        android<LibraryExtension> {
            defaultConfig {
                // Add all files from 'proguard' dir
                consumerProguardFiles.addAll(projectProguardFiles())
            }

            buildFeatures {
                buildConfig = false
                resValues = false
                androidResources = false
            }
        }
        androidFinalizeDsl<LibraryExtension> {
            defaultConfig {
                targetSdk = androidOptions.targetSdk.get()
            }
        }

        // Enable Explicit API mode for libraries by default
        if (kotlin.explicitApi == null) kotlin.explicitApi()
        afterEvaluate {
            configureExplicitApi(kotlin.explicitApi)
        }
    }
}

private fun Project.configureExplicitApi(mode: ExplicitApiMode?) {
    if (mode == null) return

    tasks.matching { it is KotlinCompile && !it.name.contains("test", ignoreCase = true) }
        .configureEach {
            val options = (this as KotlinCompile).kotlinOptions
            if (options.freeCompilerArgs.none { arg -> arg.startsWith(ARG_EXPLICIT_API) }) {
                options.freeCompilerArgs += mode.toCompilerArg()
            }
        }
}
