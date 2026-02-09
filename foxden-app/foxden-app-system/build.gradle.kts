plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(platform(project(":foxden-bom")))

    // Common modules
    implementation(project(":foxden-common:foxden-common-core"))
    implementation(project(":foxden-common:foxden-common-web"))
    implementation(project(":foxden-common:foxden-common-security"))
    implementation(project(":foxden-common:foxden-common-redis"))
    implementation(project(":foxden-common:foxden-common-log"))
    implementation(project(":foxden-common:foxden-common-idempotent"))
    implementation(project(":foxden-common:foxden-common-excel"))
    implementation(project(":foxden-common:foxden-common-oss"))
    implementation(project(":foxden-common:foxden-common-doc"))
    implementation(project(":foxden-common:foxden-common-encrypt"))

    // Domain modules
    implementation(project(":foxden-domain:foxden-domain-system"))
    implementation(project(":foxden-domain:foxden-domain-tenant"))

    // Runtime database dependencies
    runtimeOnly("org.postgresql:postgresql")

    // Spring Boot starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-devtools")

    // Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

// Java toolchain
configure<JavaPluginExtension> {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

// Kotlin compiler options
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

// JUnit 5
tasks.withType<Test> {
    useJUnitPlatform()
}

// Headless mode
tasks.withType<JavaExec> {
    systemProperty("java.awt.headless", "true")
}

// Spring Boot configuration
springBoot {
    mainClass.set(null as String?)
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    enabled = false
}

tasks.withType<Jar> {
    enabled = true
    archiveClassifier.set("")
}
