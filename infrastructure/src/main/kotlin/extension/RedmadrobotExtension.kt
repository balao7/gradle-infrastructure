package com.redmadrobot.build.extension

import com.redmadrobot.build.internal.findByName
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.tasks.testing.junitplatform.JUnitPlatformOptions
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject
import kotlin.properties.ReadOnlyProperty

public abstract class RedmadrobotExtension @Inject constructor(objects: ObjectFactory) : ExtensionAware {

    public companion object {
        /** Extension name. */
        public const val NAME: String = "redmadrobot"

        // Relative to root project directory.
        internal const val DEFAULT_CONFIGS_DIR = "config/"

        // Relative to root project build directory.
        internal const val DEFAULT_REPORTS_DIR = "reports/"

        /**
         * Provides delegate to add an extra property to [RedmadrobotExtension].
         *
         * It may be useful to use package `org.gradle.kotlin.dsl` for delegated properties because
         * members from this package are imported by default and declared property can be used without import.
         * This package is used by Gradle for generated accessors.
         *
         * ```
         * package org.gradle.kotlin.dsl
         *
         * val RedmadrobotExtension.android: AndroidOptions by RedmadrobotExtension.extensionProperty()
         * ```
         */
        public inline fun <reified V : Any> extensionProperty(): ReadOnlyProperty<RedmadrobotExtension, V> {
            return ReadOnlyProperty { thisRef, property ->
                thisRef.extensions.findByName<V>(property.name)
                    ?: thisRef.extensions.create(property.name)
            }
        }
    }

    /** Kotlin version that should be used for all projects. */
    @Deprecated(
        level = DeprecationLevel.ERROR,
        message = "This option have not effect anymore. " +
            "Remove it and use `kotlin-bom` to align Kotlin version across all dependencies.",
    )
    @Suppress("unused_parameter")
    public var kotlinVersion: String
        set(value) = error("You should not use this.")
        get() = error("You should not use this.")

    /** Directory where stored configs for static analyzers. */
    public abstract val configsDir: DirectoryProperty

    /** Directory where will be stored static analyzers reports. */
    public abstract val reportsDir: DirectoryProperty

    /** Settings for publishing. */
    public val publishing: PublishingOptions = objects.newInstance()

    /** Settings for publishing. */
    public fun publishing(configure: PublishingOptions.() -> Unit) {
        publishing.configure()
    }

    /** Settings for JVM test task. */
    public val test: TestOptions = objects.newInstance()

    /** Settings for JVM test task. */
    public fun test(configure: TestOptions.() -> Unit) {
        test.configure()
    }

    /** Settings for detekt task. */
    public val detekt: DetektOptions = objects.newInstance()

    /** Settings for detekt task. */
    public fun detekt(configure: DetektOptions.() -> Unit) {
        detekt.configure()
    }
}

public open class PublishingOptions internal constructor() {

    /**
     * Enables artifacts signing before publication.
     *
     * By default tries to use gpg-agent to sign artifacts, but you can disable it with setting
     * [useGpgAgent] to false.
     * If you don't use gpg-agent, requires signatory credentials to be configured in `gradle.properties`.
     * Read more: [Signing Plugin](https://docs.gradle.org/current/userguide/signing_plugin.html#signing_plugin)
     *
     * @see useGpgAgent
     */
    public var signArtifacts: Boolean = false

    /** Use gpg-agent to sign artifacts. Has effect only if [signArtifacts] is true. */
    public var useGpgAgent: Boolean = true

    internal var configurePom: MavenPom.() -> Unit = {}

    /**
     * Configures POM file for all modules.
     * Place here only common configurations.
     */
    public fun pom(configure: MavenPom.() -> Unit) {
        configurePom = configure
    }
}

public open class TestOptions {

    /** Flag for using Junit Jupiter Platform. */
    internal var useJunitPlatform: Boolean = true
        private set

    /** Options for JUnit Platform. */
    internal val jUnitPlatformOptions by lazy { JUnitPlatformOptions() }

    public fun useJunitPlatform(testFrameworkConfigure: JUnitPlatformOptions.() -> Unit = {}) {
        useJunitPlatform = true
        testFrameworkConfigure.invoke(jUnitPlatformOptions)
    }

    public fun useJunit() {
        useJunitPlatform = false
    }
}

public open class DetektOptions {

    /** Options for detektDiff task. */
    internal var detektDiffOptions: DetektDiffOptions? = null
        private set

    /** Enable Detekt checks only for modified files provided by git (compare with [branch]). */
    public fun checkOnlyDiffWithBranch(
        branch: String,
        configure: DetektDiffOptions.() -> Unit = {},
    ) {
        require(branch.isNotBlank()) { "Base branch should not be blank." }

        detektDiffOptions = DetektDiffOptions().apply {
            configure()
            baseBranch = branch
        }
    }
}

public class DetektDiffOptions {

    /** Base branch to compare changes. */
    internal var baseBranch: String = ""

    /** List of file extensions to check. */
    public var fileExtensions: Set<String> = setOf(".kt", ".kts")
}
