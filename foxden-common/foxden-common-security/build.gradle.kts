plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    `java-library`
}

dependencies {
    // BOM for version management
    api(platform(project(":foxden-bom")))

    // Common core
    api(project(":foxden-common:foxden-common-core"))

    // Spring Security
    api("org.springframework.boot:spring-boot-starter-security")
}
