import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("com.google.devtools.ksp")
    `java-library`
}

dependencies {
    api(platform(project(":foxden-bom")))

    // 模块间依赖
    api(project(":foxden-common:foxden-common-core"))
    api(project(":foxden-common:foxden-common-json"))
    api(project(":foxden-common:foxden-common-web"))
    api(project(":foxden-common:foxden-common-redis"))

    // 社交登录依赖
    api("me.zhyd.oauth:JustAuth")

    // Spring Boot
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-configuration-processor")

    // Hutool
    api("cn.hutool:hutool-core")
    api("cn.hutool:hutool-http")

    // KSP - Jimmer
    ksp("org.babyfish.jimmer:jimmer-ksp:${property("version.jimmer")}")
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

// Kotlin 编译选项
tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

// JUnit 5 测试
tasks.withType<Test> {
    useJUnitPlatform()
}

// Java 应用 headless 模式
tasks.withType<JavaExec> {
    systemProperty("java.awt.headless", "true")
}
