import org.jetbrains.dokka.gradle.*

repositories.mavenCentral()

plugins {
    kotlin("jvm") version "2.3.10"
    kotlin("plugin.serialization") version "2.3.10" apply false
    id("org.jetbrains.dokka") version "2.1.0"
    id("org.jetbrains.dokka-javadoc") version "2.1.0"
}

subprojects {
    group = "net.typedrest"
    version = System.getenv("VERSION") ?: "1.0-SNAPSHOT"

    repositories.mavenCentral()

    fun kotlin(module: String) = "org.jetbrains.kotlin.${module}"
    apply(plugin = kotlin("jvm"))
    apply(plugin = kotlin("plugin.serialization"))
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "org.jetbrains.dokka-javadoc")

    kotlin {
        compilerOptions.allWarningsAsErrors = true
    }

    dokka {
        dokkaSourceSets.configureEach {
            // Package-level documentation
            val mdFiles = fileTree("src/main/kotlin") {
                include("**/_doc.md")
            }
            mdFiles.files.forEach { mdFile ->
                // Avoid duplicate file name conflicts
                val relativePath = mdFile.relativeTo(project.file("src/main/kotlin")).path.replace("/", "-").replace("\\", "-")
                val uniqueFile = project.layout.buildDirectory.file("tmp/dokka-includes/$relativePath").get().asFile
                uniqueFile.parentFile.mkdirs()
                mdFile.copyTo(uniqueFile, overwrite = true)
                includes.from(uniqueFile)
            }
        }
    }
}

dependencies {
    subprojects.forEach { dokka(it) }
}
