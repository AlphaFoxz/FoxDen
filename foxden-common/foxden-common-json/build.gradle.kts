plugins {
    kotlin("jvm")
    `java-library`
}

dependencies {
    // 引入 BOM，版本由 BOM 统一管理
    api(platform(project(":foxden-bom")))

    // Common Core
    api(project(":foxden-common:foxden-common-core"))

    // Jackson
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Spring Boot
    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework.boot:spring-boot-autoconfigure")

    // Hutool
    api("cn.hutool:hutool-core")
}
