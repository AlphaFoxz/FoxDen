plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

dependencies {
    api(platform(project(":foxden-bom")))
    api(project(":foxden-common:foxden-common-core"))
    api(project(":foxden-common:foxden-common-web"))
    api("org.babyfish.jimmer:jimmer-spring-boot-starter")
    api("org.babyfish.jimmer:jimmer-sql-kotlin")
    ksp("org.babyfish.jimmer:jimmer-ksp:${property("version.jimmer")}")
    api("com.fasterxml.jackson.core:jackson-annotations:2.18.2")
    api("cn.dev33:sa-token-core")
}
