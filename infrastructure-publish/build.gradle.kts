description = "Plugin to make publication as simple as possible."

gradlePlugin {
    plugins {
        register("publish-config") {
            id = "redmadrobot.publish-config"
            implementationClass = "com.redmadrobot.build.publish.PublishConfigPlugin"
        }
        register("publish") {
            id = "redmadrobot.publish"
            implementationClass = "com.redmadrobot.build.publish.PublishPlugin"
        }
    }
}

repositories {
    google()
}

dependencies {
    api(projects.infrastructureBase)

    compileOnly(libs.androidGradle)
}
