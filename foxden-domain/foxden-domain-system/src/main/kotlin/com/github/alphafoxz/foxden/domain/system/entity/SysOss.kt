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
 * OSS对象存储
 * 
 * @author wong
 */
@Entity
@Table(name = "sys_oss")
interface SysOss : CommDelFlag, CommId, CommInfo, CommTenant {
    /**
     * 文件名
     */
    @Column(name = "file_name")
    @get:Length(max = 2147483647)
    val fileName: String

    /**
     * 原名
     */
    @Column(name = "original_name")
    @get:Length(max = 2147483647)
    val originalName: String?

    /**
     * 文件后缀名
     */
    @Column(name = "file_suffix")
    @get:Length(max = 2147483647)
    val fileSuffix: String?

    /**
     * URL地址
     */
    @Column(name = "url")
    @get:Length(max = 2147483647)
    val url: String

    /**
     * 扩展字段
     */
    @Column(name = "ext1")
    @get:Length(max = 2147483647)
    val ext1: String?

    /**
     * 服务商
     */
    @Column(name = "service")
    @get:Length(max = 2147483647)
    val service: String?
}
