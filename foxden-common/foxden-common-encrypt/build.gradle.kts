plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":foxden-common:foxden-common-core"))
    implementation("cn.hutool:hutool-crypto:5.8.25")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")
}
