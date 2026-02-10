plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("com.google.devtools.ksp")
}

dependencies {
    api(platform(project(":foxden-bom")))
    api(project(":foxden-common:foxden-common-core"))
    api(project(":foxden-common:foxden-common-jimmer"))
    api(project(":foxden-common:foxden-common-web"))
    api(project(":foxden-common:foxden-common-log"))
    api(project(":foxden-common:foxden-common-excel"))
    api(project(":foxden-common:foxden-common-idempotent"))
    api(project(":foxden-domain:foxden-domain-workflow"))
    implementation("org.babyfish.jimmer:jimmer-spring-boot-starter")
    implementation("cn.dev33:sa-token-core")
    ksp("org.babyfish.jimmer:jimmer-ksp:${property("version.jimmer")}")
}
