description = "Adds support for serializing using XmlUtil (https://github.com/pdvrieze/xmlutil) instead of kotlinx.serialization's JSON format."

kotlin.jvmToolchain(21)
tasks.test { useJUnitPlatform() }

dependencies {
    api(libs.okhttp3)
    api(libs.xmlutil.serialization)
    api(project(":typedrest"))

    testImplementation(kotlin("test"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.okhttp3.mockwebserver)
}
