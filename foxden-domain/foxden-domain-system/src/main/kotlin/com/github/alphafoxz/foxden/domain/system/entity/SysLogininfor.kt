package com.github.alphafoxz.foxden.domain.system.entity

import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommDelFlag
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommId
import com.github.alphafoxz.foxden.common.jimmer.entity.comm.CommTenant
import java.time.LocalDateTime
import org.babyfish.jimmer.sql.Column
import org.babyfish.jimmer.sql.Entity
import org.babyfish.jimmer.sql.Table
import org.hibernate.validator.constraints.Length

/**
 * 系统访问记录
 * 
 * @author wong
 */
@Entity
@Table(name = "sys_logininfor")
interface SysLogininfor : CommDelFlag, CommId, CommTenant {
    /**
     * 用户账号
     */
    @Column(name = "user_name")
    @get:Length(max = 2147483647)
    val userName: String?

    /**
     * 客户端
     */
    @Column(name = "client_key")
    @get:Length(max = 2147483647)
    val clientKey: String?

    /**
     * 设备类型
     */
    @Column(name = "device_type")
    @get:Length(max = 2147483647)
    val deviceType: String?

    /**
     * 登录IP地址
     */
    @Column(name = "ipaddr")
    @get:Length(max = 2147483647)
    val ipaddr: String?

    /**
     * 登录地点
     */
    @Column(name = "login_location")
    @get:Length(max = 2147483647)
    val loginLocation: String?

    /**
     * 浏览器类型
     */
    @Column(name = "browser")
    @get:Length(max = 2147483647)
    val browser: String?

    /**
     * 操作系统
     */
    @Column(name = "os")
    @get:Length(max = 2147483647)
    val os: String?

    /**
     * 登录状态（0成功 1失败）
     */
    @Column(name = "status")
    @get:Length(max = 1)
    val status: String

    /**
     * 提示消息
     */
    @Column(name = "msg")
    @get:Length(max = 2147483647)
    val msg: String?

    /**
     * 访问时间
     */
    @Column(name = "login_time")
    val loginTime: LocalDateTime
}
