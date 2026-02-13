// Build script needs to read versions from gradle.properties
buildscript {
    extra.apply {
        set("kotlinVersion", providers.gradleProperty("version.kotlin").get())
        set("springBootVersion", providers.gradleProperty("version.springBoot").get())
    }
}

plugins {
    kotlin("jvm") version "2.3.0" apply false
    kotlin("plugin.spring") version "2.3.0" apply false
    id("org.springframework.boot") version "3.5.10" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("com.google.devtools.ksp") version "2.3.0" apply false
}

allprojects {
    group = "com.github.alphafoxz"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    // Don't apply plugins to BOM module
    if (project.name != "foxden-bom") {
        apply(plugin = "kotlin")
        apply(plugin = "java-library")

        configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }

        tasks.withType<JavaExec> {
            systemProperty("java.awt.headless", "true")
        }

        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            compilerOptions {
                freeCompilerArgs.addAll(
                    "-Xjsr305=strict"
                )
                // 启用所有可能的优化
                allWarningsAsErrors = false
            }
        }

        tasks.withType<Test> {
            useJUnitPlatform()
        }

        // Configure KSP output directory for projects that use KSP
        afterEvaluate {
            if (plugins.hasPlugin("com.google.devtools.ksp")) {
                configure<org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension> {
                    sourceSets.getByName("main") {
                        kotlin.srcDir("build/generated/ksp/main/kotlin")
                    }
                }

                // Set system property for KSP via JVM args
                tasks.withType<JavaExec> {
                    if (name.startsWith("ksp")) {
                        jvmArgs("-Djava.awt.headless=true")
                    }
                }
            }
        }
    }
}

// Set global JVM args for Gradle daemon
gradle.beforeProject {
    tasks.withType<JavaExec> {
        systemProperty("java.awt.headless", "true")
    }
}
