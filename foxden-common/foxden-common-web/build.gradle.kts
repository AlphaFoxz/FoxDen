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

    // Spring Web
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-validation")

    // Swagger/OpenAPI
    api("org.springdoc:springdoc-openapi-starter-webmvc-ui")
}
