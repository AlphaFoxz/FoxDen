package com.github.alphafoxz.foxden.common.oss.enums

import software.amazon.awssdk.services.s3.model.BucketCannedACL
import software.amazon.awssdk.services.s3.model.ObjectCannedACL

/**
 * 桶访问策略配置
 */
enum class AccessPolicyType(
    /**
     * 桶 权限类型（数据库值）
     */
    val type: String,

    /**
     * 桶 权限类型
     */
    val bucketCannedACL: BucketCannedACL,

    /**
     * 文件对象 权限类型
     */
    val objectCannedACL: ObjectCannedACL
) {
    /**
     * private
     */
    PRIVATE("0", BucketCannedACL.PRIVATE, ObjectCannedACL.PRIVATE),

    /**
     * public
     */
    PUBLIC("1", BucketCannedACL.PUBLIC_READ_WRITE, ObjectCannedACL.PUBLIC_READ_WRITE),

    /**
     * custom
     */
    CUSTOM("2", BucketCannedACL.PUBLIC_READ, ObjectCannedACL.PUBLIC_READ);

    companion object {
        fun getByType(type: String): AccessPolicyType {
            return values().find { it.type == type }
                ?: throw RuntimeException("'type' not found By $type")
        }
    }
}
