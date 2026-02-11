plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("com.google.devtools.ksp")
}

dependencies {
    api(platform(project(":foxden-bom")))
    api(project(":foxden-common:foxden-common-core"))
    api(project(":foxden-common:foxden-common-jimmer"))
    api(project(":foxden-common:foxden-common-security"))
    api(project(":foxden-common:foxden-common-oss"))
    api(project(":foxden-common:foxden-common-excel"))
    api(project(":foxden-common:foxden-common-sensitive"))
    api(project(":foxden-common:foxden-common-translation"))
    api(project(":foxden-domain:foxden-domain-tenant"))
    implementation("org.babyfish.jimmer:jimmer-spring-boot-starter")
    ksp("org.babyfish.jimmer:jimmer-ksp:${property("version.jimmer")}")
}
