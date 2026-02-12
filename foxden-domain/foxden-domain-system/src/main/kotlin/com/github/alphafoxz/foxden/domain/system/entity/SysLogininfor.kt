package com.github.alphafoxz.foxden.domain.system.entity

import org.babyfish.jimmer.jackson.JsonConverter
import org.babyfish.jimmer.jackson.LongToStringConverter
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Id
import org.babyfish.jimmer.sql.Table
import java.util.*

/**
 * 系统访问记录表 sys_logininfor
 */
@Entity
@Table(name = "sys_logininfor")
interface SysLogininfor {

    /**
     * 访问ID
     */
    @Column(name = "info_id")
    @Id
    @JsonConverter(LongToStringConverter::class)
    val id: Long

    /**
     * 租户编号
     */
    val tenantId: String?

    /**
     * 用户账号
     */
    val userName: String?

    /**
     * 客户端
     */
    val clientKey: String?

    /**
     * 设备类型
     */
    val deviceType: String?

    /**
     * 登录状态 0成功 1失败
     */
    val status: String?

    /**
     * 登录IP地址
     */
    val ipaddr: String?

    /**
     * 登录地点
     */
    val loginLocation: String?

    /**
     * 浏览器类型
     */
    val browser: String?

    /**
     * 操作系统
     */
    val os: String?

    /**
     * 提示消息
     */
    val msg: String?

    /**
     * 访问时间
     */
    val loginTime: Date?
}
