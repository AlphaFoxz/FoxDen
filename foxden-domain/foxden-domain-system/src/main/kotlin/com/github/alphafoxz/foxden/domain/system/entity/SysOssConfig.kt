package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * 对象存储配置对象 sys_oss_config
 */
@Entity
@Table(name = "sys_oss_config")
interface SysOssConfig : CommInfo {
    /**
     * 主键ID
     */
    @org.babyfish.jimmer.sql.Column(name = "oss_config_id")
    @Id
    @GeneratedValue
    val id: Long

    /**
     * 配置key
     */
    val configKey: String

    /**
     * accessKey
     */
    val accessKey: String?

    /**
     * 秘钥
     */
    val secretKey: String?

    /**
     * 桶名称
     */
    val bucketName: String?

    /**
     * 前缀
     */
    val prefix: String?

    /**
     * 访问站点
     */
    val endpoint: String?

    /**
     * 自定义域名
     */
    val domain: String?

    /**
     * 是否https（0否 1是）
     */
    @Column(name = "is_https")
    val httpsFlag: String?

    /**
     * 域
     */
    val region: String?

    /**
     * 是否默认（0=是,1=否）
     */
    val status: String?

    /**
     * 扩展字段
     */
    val ext1: String?

    /**
     * 桶权限类型(0private 1public 2custom)
     */
    val accessPolicy: String?
}
