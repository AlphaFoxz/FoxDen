plugins {
    kotlin("jvm")
}

dependencies {
    api(platform(project(":foxden-bom")))
    api(project(":foxden-common:foxden-common-core"))
    api(project(":foxden-common:foxden-common-redis"))
    api("cn.dev33:sa-token-spring-boot3-starter")
    api("cn.dev33:sa-token-jwt")
    api("cn.dev33:sa-token-core")
    api("com.github.ben-manes.caffeine:caffeine")
}
