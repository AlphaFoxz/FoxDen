plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    `java-library`
}

dependencies {
    // 引入 BOM
    api(platform(project(":foxden-bom")))

    // 模块间依赖
    api(project(":foxden-common:foxden-common-core"))

    // SpringDoc OpenAPI
    api("org.springdoc:springdoc-openapi-starter-webmvc-ui")

    // Therapi Runtime Javadoc
    api("com.github.therapi:therapi-runtime-javadoc")

    // Jackson Kotlin
    api("com.fasterxml.jackson.module:jackson-module-kotlin")
}
