plugins {
    kotlin("jvm")
    `java-library`
}

dependencies {
    // 引入 BOM，版本由 BOM 统一管理
    api(platform(project(":foxden-bom")))

    // Spring
    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework:spring-context-support")
    api("org.springframework:spring-web")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.boot:spring-boot-starter-aop")
    api("jakarta.servlet:jakarta.servlet-api")

    // Hutool
    api("cn.hutool:hutool-core")
    api("cn.hutool:hutool-http")
    api("cn.hutool:hutool-extra")

    // Commons
    api("org.apache.commons:commons-lang3")

    // Lombok (compileOnly for annotation processing)
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // MapStruct Plus
    api("io.github.linpeilie:mapstruct-plus-spring-boot-starter")

    // IP2Region
    api("org.lionsoul:ip2region")

    // Dotenv
    api("io.github.cdimascio:dotenv-java")

    // Spring Boot Configuration Processor
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}
