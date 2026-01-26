plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    `java-library`
}

dependencies {
    // Common core
    api(project(":foxden-common:foxden-common-core"))

    // Spring Email (placeholder - empty initially)
    // api("org.springframework.boot:spring-boot-starter-mail")
}
