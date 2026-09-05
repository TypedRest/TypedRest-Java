description = "Adds support for serializing using Jackson XML (https://github.com/FasterXML/jackson-dataformat-xml) instead of kotlinx.serialization."

kotlin.jvmToolchain(21)
tasks.test { useJUnitPlatform() }

dependencies {
    api(libs.okhttp3)
    api(libs.jackson.dataformat.xml)
    implementation(libs.jackson.kotlin)
    api(project(":typedrest"))

    testImplementation(kotlin("test"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.okhttp3.mockwebserver)
}
