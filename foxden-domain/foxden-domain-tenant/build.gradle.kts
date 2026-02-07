plugins {
    kotlin("jvm")
}

dependencies {
    api(platform(project(":foxden-bom")))
    api(project(":foxden-common:foxden-common-core"))
    api(project(":foxden-common:foxden-common-jimmer"))
    implementation("org.babyfish.jimmer:jimmer-spring-boot-starter")
}
