repositories.mavenCentral()

plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0" apply false
    id("org.jetbrains.dokka") version "1.9.20" apply false
}

subprojects {
    group = "net.typedrest"
    version = System.getenv("VERSION") ?: "1.0-SNAPSHOT"

    repositories.mavenCentral()

    fun kotlin(module: String) = "org.jetbrains.kotlin.${module}"
    apply(plugin = kotlin("jvm"))
    apply(plugin = kotlin("plugin.serialization"))
    apply(plugin = "org.jetbrains.dokka")

    kotlin {
        compilerOptions.allWarningsAsErrors = true
    }
}
