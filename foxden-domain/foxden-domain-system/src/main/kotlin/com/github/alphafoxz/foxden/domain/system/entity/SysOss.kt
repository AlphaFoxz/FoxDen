package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*

/**
 * OSS对象存储对象
 */
@Entity
@Table(name = "sys_oss")
interface SysOss : CommDelFlag, CommInfo, CommTenant {
    /**
     * 主键ID
     */
    @org.babyfish.jimmer.sql.Column(name = "oss_id")
    @Id
    @GeneratedValue
    val id: Long

    /**
     * 文件名
     */
    val fileName: String

    /**
     * 原名
     */
    val originalName: String?

    /**
     * 文件后缀名
     */
    val fileSuffix: String?

    /**
     * URL地址
     */
    val url: String?

    /**
     * 扩展字段
     */
    val ext1: String?

    /**
     * 服务商
     */
    val service: String?
}
