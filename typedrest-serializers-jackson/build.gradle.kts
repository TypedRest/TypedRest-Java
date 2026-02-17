description = "Adds support for serializing using Jackson (https://github.com/FasterXML/jackson) instead of kotlinx.serialization."

kotlin.jvmToolchain(21)
tasks.test { useJUnitPlatform() }

dependencies {
    api(libs.okhttp3)
    api(libs.jackson.databind)
    implementation(libs.jackson.kotlin)
    api(project(":typedrest"))

    testImplementation(kotlin("test"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.okhttp3.mockwebserver)
}
