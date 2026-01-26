plugins {
    kotlin("jvm")
    `java-library`
}

dependencies {
    // BOM for version management
    api(platform(project(":foxden-bom")))

    // Kotlin
    api("org.jetbrains.kotlin:kotlin-reflect")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Jackson for JSON
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Validation
    api("jakarta.validation:jakarta.validation-api")
}
