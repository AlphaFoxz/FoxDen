plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.google.devtools.ksp")
}

dependencies {
    // BOM for version management
    implementation(platform(project(":foxden-bom")))

    // Common modules
    implementation(project(":foxden-common:foxden-common-core"))
    implementation(project(":foxden-common:foxden-common-jimmer"))
    implementation(project(":foxden-common:foxden-common-web"))
    implementation(project(":foxden-common:foxden-common-security"))

    // Domain modules
    implementation(project(":foxden-domain:foxden-domain-system"))
    implementation(project(":foxden-domain:foxden-domain-tenant"))

    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Development tools (disabled due to KSP compatibility issues)
    // developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Database
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")

    // Jimmer
    ksp("org.babyfish.jimmer:jimmer-ksp:${property("version.jimmer")}")
    runtimeOnly("org.babyfish.jimmer:jimmer-client-swagger")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    mainClass.set("com.github.alphafoxz.foxden.app.admin.FoxdenAdminApplicationKt")
}

tasks.withType<Jar> {
    enabled = false
}
