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
    api(project(":foxden-common:foxden-common-redis"))

    // SMS4J
    api("org.dromara.sms4j:sms4j-spring-boot-starter")
}
