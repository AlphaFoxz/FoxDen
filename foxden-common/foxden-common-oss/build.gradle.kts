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
    api(project(":foxden-common:foxden-common-json"))
    api(project(":foxden-common:foxden-common-redis"))

    // AWS SDK for S3 protocol
    api("software.amazon.awssdk:s3")
    api("software.amazon.awssdk:netty-nio-client")
    api("software.amazon.awssdk:s3-transfer-manager")
    api("software.amazon.awssdk:apache-client")

    // MinIO
    api("io.minio:minio")

    // Aliyun OSS
    api("com.aliyun.oss:aliyun-sdk-oss")

    // Qcloud COS
    api("com.qcloud:cos_api")
}
