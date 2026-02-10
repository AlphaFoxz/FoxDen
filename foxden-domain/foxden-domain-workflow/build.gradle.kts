plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("com.google.devtools.ksp")
}

dependencies {
    api(platform(project(":foxden-bom")))
    api(project(":foxden-common:foxden-common-core"))
    api(project(":foxden-common:foxden-common-jimmer"))
    implementation(project(":foxden-common:foxden-common-security"))
    implementation("org.babyfish.jimmer:jimmer-spring-boot-starter")
    ksp("org.babyfish.jimmer:jimmer-ksp:${property("version.jimmer")}")

    // WarmFlow 工作流引擎
    implementation("org.dromara.warm:warm-flow-mybatis-plus-sb3-starter")
}
