plugins {
    `java-platform`
}

dependencies {
    constraints {
        // Kotlin
        api("org.jetbrains.kotlin:kotlin-reflect:${property("version.kotlin")}")
        api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${property("version.kotlin")}")

        // Jimmer
        api("org.babyfish.jimmer:jimmer-spring-boot-starter:${property("version.jimmer")}")
        api("org.babyfish.jimmer:jimmer-ksp:${property("version.jimmer")}")
        api("org.babyfish.jimmer:jimmer-sql:${property("version.jimmer")}")
        api("org.babyfish.jimmer:jimmer-sql-kotlin:${property("version.jimmer")}")
        api("org.babyfish.jimmer:jimmer-client-swagger:${property("version.jimmer")}")

        // Database
        api("org.postgresql:postgresql:${property("version.postgresql")}")
        api("com.h2database:h2:${property("version.h2")}")

        // Documentation
        api("org.springdoc:springdoc-openapi-starter-webmvc-ui:${property("version.springdoc")}")

        // Validation
        api("jakarta.validation:jakarta.validation-api:${property("version.jakartaValidation")}")

        // Jackson (Spring Boot 3.4.1 uses Jackson 2.18.2)
        api("com.fasterxml.jackson.core:jackson-databind:2.18.2")
        api("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
        api("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.18.2")
        api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.2")

        // Spring Boot
        api("org.springframework.boot:spring-boot-starter:${property("version.springBoot")}")
        api("org.springframework.boot:spring-boot-starter-web:${property("version.springBoot")}")
        api("org.springframework.boot:spring-boot-starter-validation:${property("version.springBoot")}")
        api("org.springframework.boot:spring-boot-starter-security:${property("version.springBoot")}")
        api("org.springframework.boot:spring-boot-starter-cache:${property("version.springBoot")}")
        api("org.springframework.boot:spring-boot-starter-test:${property("version.springBoot")}")
        api("org.springframework.boot:spring-boot-devtools:${property("version.springBoot")}")

        // Spring Framework
        api("org.springframework:spring-context:${property("version.spring")}")
        api("org.springframework:spring-tx:${property("version.spring")}")
        api("org.springframework:spring-web:${property("version.spring")}")
        api("org.springframework:spring-webmvc:${property("version.spring")}")
    }
}
