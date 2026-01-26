plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    `java-library`
    id("com.google.devtools.ksp")
}

dependencies {
    // BOM for version management
    api(platform(project(":foxden-bom")))

    // Common Jimmer
    api(project(":foxden-common:foxden-common-jimmer"))

    // Domain Infrastructure
    api(project(":foxden-domain:foxden-domain-infrastructure"))

    // Jimmer
    api("org.babyfish.jimmer:jimmer-spring-boot-starter")
    ksp("org.babyfish.jimmer:jimmer-ksp:${property("version.jimmer")}")

    // Spring
    api("org.springframework:spring-context")
    api("org.springframework:spring-tx")

    // Validation (for @Length annotation on entities)
    api("org.springframework.boot:spring-boot-starter-validation")
}
