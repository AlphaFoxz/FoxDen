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
    api(project(":foxden-common:foxden-common-json"))
    api(project(":foxden-common:foxden-common-security"))
    api(project(":foxden-common:foxden-common-web"))

    // AspectJ
    implementation("org.springframework.boot:spring-boot-starter-aop")
}
