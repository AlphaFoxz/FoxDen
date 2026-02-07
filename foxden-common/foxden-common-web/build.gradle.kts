plugins {
    kotlin("jvm")
    `java-library`
}

dependencies {
    // 引入 BOM，版本由 BOM 统一管理
    api(platform(project(":foxden-bom")))

    // Common modules
    api(project(":foxden-common:foxden-common-core"))
    api(project(":foxden-common:foxden-common-json"))

    // Spring Web
    api("org.springframework.boot:spring-boot-starter-web") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
    api("org.springframework.boot:spring-boot-starter-undertow")
    api("org.springframework.boot:spring-boot-starter-actuator")

    // Hutool
    api("cn.hutool:hutool-captcha")
    api("cn.hutool:hutool-crypto")
}
