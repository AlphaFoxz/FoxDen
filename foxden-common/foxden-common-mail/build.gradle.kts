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

    // Jakarta Mail
    api("jakarta.mail:jakarta.mail-api")
    api("org.eclipse.angus:jakarta.mail")
}
