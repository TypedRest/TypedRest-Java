kotlin.jvmToolchain(21)
tasks.test { useJUnitPlatform() }

dependencies {
    api(libs.okhttp3)
    api(libs.moshi)
    implementation(libs.moshi.kotlin)
    api(project(":typedrest"))

    testImplementation(kotlin("test"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.okhttp3.mockwebserver)
}
