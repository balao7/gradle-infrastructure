package com.redmadrobot.build.extension

import org.gradle.api.Project

/** Returns `true` if version contains `-SNAPSHOT` suffix. */
val Project.isSnapshotVersion: Boolean
    get() = version.toString().endsWith("-SNAPSHOT")

/** Returns `true` if build is running on CI. */
val isRunningOnCi: Boolean
    get() = System.getenv("CI") == "true"

/**
 * Checks that credentials for the repository with given [name] are exist.
 *
 * See: [Handling credentials](https://docs.gradle.org/current/userguide/declaring_repositories.html#sec:handling_credentials)
 */
fun Project.credentialsExist(name: String): Boolean {
    return hasProperty("${name}Username") && hasProperty("${name}Password")
}
