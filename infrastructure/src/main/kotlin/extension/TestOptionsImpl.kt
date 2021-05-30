package com.redmadrobot.build.extension

import org.gradle.api.provider.Property
import org.gradle.api.tasks.testing.TestFrameworkOptions
import org.gradle.api.tasks.testing.junit.JUnitOptions
import org.gradle.api.tasks.testing.junitplatform.JUnitPlatformOptions

@Suppress("LeakingThis")
public abstract class TestOptionsImpl : TestOptions {

    abstract override val useJunitPlatform: Property<Boolean>

    /** Configurator for Test Framework. */
    internal abstract val configuration: Property<TestFrameworkOptions.() -> Unit>

    override fun useJunitPlatform(configure: JUnitPlatformOptions.() -> Unit) {
        useJunitPlatform.set(true)
        configuration.set { (this as JUnitPlatformOptions).configure() }
    }

    override fun useJunit(configure: JUnitOptions.() -> Unit) {
        useJunitPlatform.set(false)
        configuration.set { (this as JUnitOptions).configure() }
    }

    init {
        useJunitPlatform
            .convention(true)
            .finalizeValueOnRead()
        configuration
            .convention { /* no-op */ }
            .finalizeValueOnRead()
    }
}
