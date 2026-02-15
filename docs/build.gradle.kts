plugins {
    kotlin("jvm") apply false
}

dependencies {
    dokka(project(":typedrest:"))
    dokka(project(":typedrest-serializers-jackson:"))
    dokka(project(":typedrest-serializers-moshi:"))
}

dokka {
    moduleName.set("TypedRest")
}
