plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp")
}

dependencies {
    api(platform(project(":foxden-bom")))
    api(project(":foxden-common:foxden-common-core"))
    api(project(":foxden-common:foxden-common-jimmer"))
    api(project(":foxden-common:foxden-common-oss"))
    api(project(":foxden-domain:foxden-domain-tenant"))
    implementation("org.babyfish.jimmer:jimmer-spring-boot-starter")
    ksp("org.babyfish.jimmer:jimmer-ksp:${property("version.jimmer")}")
}
