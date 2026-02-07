package com.github.alphafoxz.foxden.domain.system.vo

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 登录日志视图对象 sys_logininfor
 *
 * @author Lion Li
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class SysLogininforVo(
    /**
     * 序号
     */
    var infoId: Long? = null,

    /**
     * 用户账号
     */
    var userName: String? = null,

    /**
     * 登录IP地址
     */
    var ipaddr: String? = null,

    /**
     * 登录地点
     */
    var loginLocation: String? = null,

    /**
     * 浏览器类型
     */
    var browser: String? = null,

    /**
     * 操作系统
     */
    var os: String? = null,

    /**
     * 登录状态（0成功 1失败）
     */
    var status: String? = null,

    /**
     * 提示消息
     */
    var msg: String? = null,

    /**
     * 访问时间
     */
    var loginTime: java.time.LocalDateTime? = null
)
