package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.*
import org.babyfish.jimmer.sql.*
import java.util.Date

/**
 * 系统访问记录表 sys_logininfor
 */
@Entity
interface SysLogininfor : CommId {
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
