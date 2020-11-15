package com.redmadrobot.build

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

class AndroidLibraryPlugin : BaseAndroidPlugin() {
    override fun apply(target: Project) = with(target) {
        apply(plugin = "com.android.library")
        super.apply(target)

        kotlinCompile {
            kotlinOptions.freeCompilerArgs += "-Xexplicit-api=strict"
        }

        android<LibraryExtension> {
            buildFeatures {
                buildConfig = false
                resValues = false
                androidResources = false
            }
        }

        configureKotlinDependencies()
    }
}
