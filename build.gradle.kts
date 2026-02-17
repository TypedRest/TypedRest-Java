import org.jetbrains.dokka.gradle.*

group = "net.typedrest"

repositories.mavenCentral()

plugins {
    kotlin("jvm") version "2.3.10"
    kotlin("plugin.serialization") version "2.3.10" apply false
    id("org.jetbrains.dokka") version "2.1.0"
    id("org.jetbrains.dokka-javadoc") version "2.1.0"
    id("maven-publish")
    id("signing")
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
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
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    kotlin {
        compilerOptions.allWarningsAsErrors = true
    }

    dokka {
        dokkaSourceSets.configureEach {
            // Package-level documentation
            val mdFiles = fileTree("src/main") {
                include("**/_doc.md")
            }
            mdFiles.files.forEach { mdFile ->
                // Avoid duplicate file name conflicts
                val relativePath = mdFile.relativeTo(project.file("src/main")).path.replace("/", "-").replace("\\", "-")
                val uniqueFile = project.layout.buildDirectory.file("tmp/dokka-includes/$relativePath").get().asFile
                uniqueFile.parentFile.mkdirs()
                mdFile.copyTo(uniqueFile, overwrite = true)
                includes.from(uniqueFile)
            }
        }
    }

    java {
        withSourcesJar()
        withJavadocJar()
    }

    tasks.named<Jar>("javadocJar") {
        from(tasks.dokkaGeneratePublicationJavadoc.flatMap { it.outputDirectory })
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])

                pom {
                    name.set(project.name)
                    description.set("TypedRest helps you build type-safe, fluent-style REST API clients.")
                    url.set("https://typedrest.net/")

                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }

                    developers {
                        developer {
                            name.set("Bastian Eicher")
                            url.set("https://github.com/bastianeicher")
                        }
                    }

                    scm {
                        connection.set("scm:git:https://github.com/TypedRest/TypedRest-Java.git")
                        developerConnection.set("scm:git:git@github.com:TypedRest/TypedRest-Java.git")
                        url.set("https://github.com/TypedRest/TypedRest-Java")
                    }
                }
            }
        }

        configure<SigningExtension> {
            val gpgKey = System.getenv("GPG_KEY")
            if (gpgKey != null) {
                useInMemoryPgpKeys(gpgKey, "")
                sign(the<PublishingExtension>().publications["maven"])
            }
        }

        repositories {
            maven {
                name = "GitHub"
                url = uri("https://maven.pkg.github.com/TypedRest/TypedRest-Java")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}

dependencies {
    subprojects.forEach { dokka(it) }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
        }
    }
}
