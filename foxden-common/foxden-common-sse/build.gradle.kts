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
    api(project(":foxden-common:foxden-common-redis"))
    api(project(":foxden-common:foxden-common-security"))
    api(project(":foxden-common:foxden-common-json"))

    // Spring Web (for SseEmitter)
    api("org.springframework.boot:spring-boot-starter-web")
}

afterEvaluate {
    if (plugins.hasPlugin("com.google.devtools.ksp")) {
        configure<org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension> {
            sourceSets.getByName("main") {
                kotlin.srcDir("build/generated/ksp/main/kotlin")
            }
        }
    }
}
