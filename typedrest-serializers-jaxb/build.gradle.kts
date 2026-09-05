description = "Adds support for serializing using JAXB (https://github.com/eclipse-ee4j/jaxb-ri) instead of kotlinx.serialization."

kotlin.jvmToolchain(21)
tasks.test { useJUnitPlatform() }

dependencies {
    api(libs.okhttp3)
    api(libs.jaxb.api)
    runtimeOnly(libs.jaxb.runtime)
    api(project(":typedrest"))

    testImplementation(kotlin("test"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.okhttp3.mockwebserver)
}
