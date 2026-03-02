package com.github.alphafoxz.foxden.domain.system.vo

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated
import com.alibaba.excel.annotation.ExcelProperty
import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 登录日志视图对象 sys_logininfor
 *
 * @author Lion Li
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ExcelIgnoreUnannotated
data class SysLogininforVo(
    /**
     * 序号
     */
    @ExcelProperty(value = ["序号"])
    var infoId: Long? = null,

    /**
     * 用户账号
     */
    @ExcelProperty(value = ["用户账号"])
    var userName: String? = null,

    /**
     * 客户端
     */
    @ExcelProperty(value = ["客户端"])
    var clientKey: String? = null,

    /**
     * 设备类型
     */
    @ExcelProperty(value = ["设备类型"])
    var deviceType: String? = null,

    /**
     * 登录IP地址
     */
    @ExcelProperty(value = ["登录地址"])
    var ipaddr: String? = null,

    /**
     * 登录地点
     */
    @ExcelProperty(value = ["登录地点"])
    var loginLocation: String? = null,

    /**
     * 浏览器类型
     */
    @ExcelProperty(value = ["浏览器"])
    var browser: String? = null,

    /**
     * 操作系统
     */
    @ExcelProperty(value = ["操作系统"])
    var os: String? = null,

    /**
     * 登录状态（0成功 1失败）
     */
    @ExcelProperty(value = ["登录状态"])
    var status: String? = null,

    /**
     * 提示消息
     */
    @ExcelProperty(value = ["提示消息"])
    var msg: String? = null,

    /**
     * 访问时间
     */
    @ExcelProperty(value = ["访问时间"])
    var loginTime: java.time.LocalDateTime? = null
)
