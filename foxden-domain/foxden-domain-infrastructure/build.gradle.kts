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

    // Jimmer
    api("org.babyfish.jimmer:jimmer-spring-boot-starter")
    ksp("org.babyfish.jimmer:jimmer-ksp:${property("version.jimmer")}")

    // Spring
    api("org.springframework:spring-tx")
}
