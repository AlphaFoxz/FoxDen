plugins {
    `java-platform`
}

dependencies {
    constraints {
        // Kotlin
        api("org.jetbrains.kotlin:kotlin-reflect:${property("version.kotlin")}")
        api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${property("version.kotlin")}")

        // Spring Boot
        api("org.springframework.boot:spring-boot-starter:${property("version.springBoot")}")
        api("org.springframework.boot:spring-boot-starter-web:${property("version.springBoot")}")
        api("org.springframework.boot:spring-boot-starter-validation:${property("version.springBoot")}")
        api("org.springframework.boot:spring-boot-starter-aop:${property("version.springBootAop")}")
        api("org.springframework.boot:spring-boot-starter-security:${property("version.springBoot")}")
        api("org.springframework.boot:spring-boot-starter-cache:${property("version.springBoot")}")
        api("org.springframework.boot:spring-boot-starter-test:${property("version.springBoot")}")
        api("org.springframework.boot:spring-boot-devtools:${property("version.springBoot")}")
        api("org.springframework.boot:spring-boot-configuration-processor:${property("version.springBoot")}")
        api("org.springframework.boot:spring-boot-properties-migrator:${property("version.springBoot")}")
        api("org.springframework.boot:spring-boot-starter-undertow:${property("version.springBoot")}")
        api("org.springframework.boot:spring-boot-starter-actuator:${property("version.springBoot")}")

        // Spring Framework
        api("org.springframework:spring-context:${property("version.spring")}")
        api("org.springframework:spring-context-support:${property("version.spring")}")
        api("org.springframework:spring-tx:${property("version.spring")}")
        api("org.springframework:spring-web:${property("version.spring")}")
        api("org.springframework:spring-webmvc:${property("version.spring")}")

        // Jimmer
        api("org.babyfish.jimmer:jimmer-spring-boot-starter:${property("version.jimmer")}")
        api("org.babyfish.jimmer:jimmer-ksp:${property("version.jimmer")}")
        api("org.babyfish.jimmer:jimmer-sql:${property("version.jimmer")}")
        api("org.babyfish.jimmer:jimmer-sql-kotlin:${property("version.jimmer")}")
        api("org.babyfish.jimmer:jimmer-kotlin:${property("version.jimmer")}")
        api("org.babyfish.jimmer:jimmer-client-swagger:${property("version.jimmer")}")

        // Database
        api("org.postgresql:postgresql:${property("version.postgresql")}")

        // Documentation
        api("org.springdoc:springdoc-openapi-starter-webmvc-ui:${property("version.springdoc")}")

        // Validation
        api("jakarta.validation:jakarta.validation-api:${property("version.jakartaValidation")}")

        // Jackson
        api("com.fasterxml.jackson.core:jackson-databind:2.18.2")
        api("com.fasterxml.jackson.core:jackson-annotations:2.18.2")
        api("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
        api("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.18.2")
        api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.2")

        // Sa-Token
        api("cn.dev33:sa-token-spring-boot3-starter:${property("version.saToken")}")
        api("cn.dev33:sa-token-core:${property("version.saToken")}")
        api("cn.dev33:sa-token-jwt:${property("version.saToken")}")
        api("cn.dev33:sa-token-redis-jackson:${property("version.saToken")}")

        // Hutool
        api("cn.hutool:hutool-core:${property("version.hutool")}")
        api("cn.hutool:hutool-http:${property("version.hutool")}")
        api("cn.hutool:hutool-extra:${property("version.hutool")}")
        api("cn.hutool:hutool-captcha:${property("version.hutool")}")
        api("cn.hutool:hutool-crypto:${property("version.hutool")}")

        // Commons
        api("org.apache.commons:commons-lang3:${property("version.commonsLang3")}")
        api("com.github.ben-manes.caffeine:caffeine:3.1.8")

        // Servlet
        api("jakarta.servlet:jakarta.servlet-api:${property("version.jakartaServlet")}")

        // Lombok
        api("org.projectlombok:lombok:${property("version.lombok")}")

        // MapStruct Plus
        api("io.github.linpeilie:mapstruct-plus-spring-boot-starter:${property("version.mapstructPlus")}")

        // IP2Region
        api("org.lionsoul:ip2region:${property("version.ip2region")}")

        // Dotenv
        api("io.github.cdimascio:dotenv-java:${property("version.dotenv")}")

        // Jakarta Mail
        api("jakarta.mail:jakarta.mail-api:2.1.3")
        api("org.eclipse.angus:jakarta.mail:2.0.3")

        // SpringDoc OpenAPI
        api("org.springdoc:springdoc-openapi-starter-webmvc-ui:${property("version.springdoc")}")
        api("com.github.therapi:therapi-runtime-javadoc:0.15.0")

        // SMS4J
        api("org.dromara.sms4j:sms4j-spring-boot-starter:${property("version.sms4j")}")

        // EasyExcel
        api("com.alibaba:easyexcel:${property("version.easyexcel")}")

        // AWS SDK for S3 protocol compatibility
        api("software.amazon.awssdk:s3:${property("version.awssdk")}")
        api("software.amazon.awssdk:netty-nio-client:${property("version.awssdk")}")
        api("software.amazon.awssdk:s3-transfer-manager:${property("version.awssdk")}")
        api("software.amazon.awssdk:apache-client:${property("version.awssdk")}")

        // OSS
        api("io.minio:minio:${property("version.minio")}")
        api("com.aliyun.oss:aliyun-sdk-oss:${property("version.aliyunOss")}")
        api("com.qcloud:cos_api:${property("version.qcloudCos")}")

        // Redisson
        api("org.redisson:redisson-spring-boot-starter:${property("version.redisson")}")
        api("com.baomidou:lock4j-redisson-spring-boot-starter:${property("version.lock4j")}")

        // Social login
        api("me.zhyd.oauth:JustAuth:${property("version.justAuth")}")
    }
}
