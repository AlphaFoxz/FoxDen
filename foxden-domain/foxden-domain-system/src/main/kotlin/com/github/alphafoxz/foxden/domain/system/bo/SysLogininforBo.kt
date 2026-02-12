package com.github.alphafoxz.foxden.domain.system.bo

import jakarta.validation.constraints.Size

/**
 * 登录日志业务对象 sys_logininfor
 *
 * @author Lion Li
 */
data class SysLogininforBo(
    /**
     * 租户ID
     */
    var tenantId: String? = null,

    /**
     * 序号
     */
    var infoId: Long? = null,

    /**
     * 用户账号
     */
    var userName: String? = null,

    /**
     * 客户端密钥
     */
    var clientKey: String? = null,

    /**
     * 设备类型
     */
    var deviceType: String? = null,

    /**
     * 登录IP地址
     */
    @get:Size(min = 0, max = 128, message = "IP地址长度不能超过{max}个字符")
    var ipaddr: String? = null,

    /**
     * 登录地点
     */
    @get:Size(min = 0, max = 255, message = "登录地点长度不能超过{max}个字符")
    var loginLocation: String? = null,

    /**
     * 浏览器类型
     */
    @get:Size(min = 0, max = 50, message = "浏览器类型不能超过{max}个字符")
    var browser: String? = null,

    /**
     * 操作系统
     */
    @get:Size(min = 0, max = 50, message = "操作系统类型不能超过{max}个字符")
    var os: String? = null,

    /**
     * 登录状态（0成功 1失败）
     */
    var status: String? = null,

    /**
     * 提示消息
     */
    @get:Size(min = 0, max = 255, message = "提示消息不能超过{max}个字符")
    var msg: String? = null,

    /**
     * 访问时间
     */
    var loginTime: java.time.LocalDateTime? = null,

    /**
     * 开始时间
     */
    var beginTime: String? = null,

    /**
     * 结束时间
     */
    var endTime: String? = null
)
