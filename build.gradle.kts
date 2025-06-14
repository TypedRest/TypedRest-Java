import org.jetbrains.dokka.gradle.*

repositories.mavenCentral()

plugins {
    kotlin("jvm") version "2.1.21"
    kotlin("plugin.serialization") version "2.1.21" apply false
    id("org.jetbrains.dokka") version "1.9.20"
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

    fun NamedDomainObjectContainer<GradleDokkaSourceSetBuilder>.addMarkdown() = configureEach {
        includes.from(project.files(), fileTree("src/main/kotlin").include("**/_doc.md"))
    }
    tasks.withType<DokkaTask>().configureEach { dokkaSourceSets.addMarkdown() }
    tasks.withType<DokkaTaskPartial>().configureEach { dokkaSourceSets.addMarkdown() }
}
