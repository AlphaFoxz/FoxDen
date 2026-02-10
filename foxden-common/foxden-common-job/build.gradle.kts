plugins {
    kotlin("jvm")
    `java-library`
}

dependencies {
    // 引入 BOM，版本由 BOM 统一管理
    api(platform(project(":foxden-bom")))

    // Common modules
    api(project(":foxden-common:foxden-common-core"))

    // SnailJob client
    api("com.aizuda:snail-job-client-starter")
    api("com.aizuda:snail-job-client-job-core")

    // Spring Boot AutoConfiguration
    api("org.springframework.boot:spring-boot-autoconfigure")
}
