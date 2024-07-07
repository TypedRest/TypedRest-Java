kotlin.jvmToolchain(21)
tasks.test { useJUnitPlatform() }

dependencies {
    api(libs.okhttp3)
    implementation(libs.uri.templates)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(kotlin("test"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.okhttp3.mockwebserver)
}
