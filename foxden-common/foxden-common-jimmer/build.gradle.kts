plugins {
    kotlin("jvm")
    `java-library`
    id("com.google.devtools.ksp")
}

dependencies {
    // BOM for version management
    api(platform(project(":foxden-bom")))

    // Common core
    api(project(":foxden-common:foxden-common-core"))

    // Jimmer - must use sql-kotlin for KSP to generate Draft classes for traits
    api("org.babyfish.jimmer:jimmer-sql")
    api("org.babyfish.jimmer:jimmer-sql-kotlin")
    ksp("org.babyfish.jimmer:jimmer-ksp:${property("version.jimmer")}")
}
