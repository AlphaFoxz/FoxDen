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

    // Redisson
    api("org.redisson:redisson-spring-boot-starter")

    // 锁4j
    api("com.baomidou:lock4j-redisson-spring-boot-starter")

    // Caffeine 本地缓存
    api("com.github.ben-manes.caffeine:caffeine")

    // Jackson JSR310
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
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
