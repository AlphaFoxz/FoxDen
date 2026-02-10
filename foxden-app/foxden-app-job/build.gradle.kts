plugins {
    kotlin("jvm")
}

dependencies {
    api(platform(project(":foxden-bom")))
    implementation(project(":foxden-common:foxden-common-job"))
    implementation(project(":foxden-common:foxden-common-core"))
    implementation(project(":foxden-common:foxden-common-json"))
}
