package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommId
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommInfo
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommTenant
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table
import org.hibernate.validator.constraints.Length

/**
 * 对象存储配置
 * 
 * @author wong
 */
@Entity
@Table(name = "sys_oss_config")
interface SysOssConfig : CommDelFlag, CommId, CommInfo, CommTenant {
    /**
     * 配置key
     */
    @Column(name = "config_key")
    @get:Length(max = 2147483647)
    val configKey: String

    /**
     * accessKey
     */
    @Column(name = "access_key")
    @get:Length(max = 2147483647)
    val accessKey: String

    /**
     * 秘钥
     */
    @Column(name = "secret_key")
    @get:Length(max = 2147483647)
    val secretKey: String

    /**
     * 桶名称
     */
    @Column(name = "bucket_name")
    @get:Length(max = 2147483647)
    val bucketName: String

    /**
     * 前缀
     */
    @Column(name = "prefix")
    @get:Length(max = 2147483647)
    val prefix: String?

    /**
     * 访问站点
     */
    @Column(name = "endpoint")
    @get:Length(max = 2147483647)
    val endpoint: String

    /**
     * 自定义域名
     */
    @Column(name = "domain")
    @get:Length(max = 2147483647)
    val domain: String?

    /**
     * 是否https
     */
    @Column(name = "https")
    val https: Boolean?

    /**
     * 域
     */
    @Column(name = "region")
    @get:Length(max = 2147483647)
    val region: String?

    /**
     * 桶权限类型(0=private 1=public 2=custom)
     */
    @Column(name = "access_policy")
    @get:Length(max = 1)
    val accessPolicy: String

    /**
     * 是否默认（0=是,1=否）
     */
    @Column(name = "status")
    @get:Length(max = 1)
    val status: String

    /**
     * 扩展字段
     */
    @Column(name = "ext1")
    @get:Length(max = 2147483647)
    val ext1: String?

    /**
     * 备注
     */
    @Column(name = "remark")
    @get:Length(max = 2147483647)
    val remark: String?
}
