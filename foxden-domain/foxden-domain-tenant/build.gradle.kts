plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("com.google.devtools.ksp")
}

dependencies {
    api(platform(project(":foxden-bom")))
    api(project(":foxden-common:foxden-common-core"))
    api(project(":foxden-common:foxden-common-jimmer"))
    implementation("org.babyfish.jimmer:jimmer-spring-boot-starter")
    ksp("org.babyfish.jimmer:jimmer-ksp:${property("version.jimmer")}")
}
