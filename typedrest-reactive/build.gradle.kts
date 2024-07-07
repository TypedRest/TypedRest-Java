kotlin.jvmToolchain(21)
tasks.test { useJUnitPlatform() }

dependencies {
    api(libs.okhttp3)
    api(libs.rxkotlin)
    api(project(":typedrest"))
    implementation(libs.kotlinx.serialization.json)

    testImplementation(kotlin("test"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.okhttp3.mockwebserver)
}
