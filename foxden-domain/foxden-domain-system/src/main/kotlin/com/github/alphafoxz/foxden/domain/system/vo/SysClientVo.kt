package com.github.alphafoxz.foxden.domain.system.vo

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated
import com.alibaba.excel.annotation.ExcelProperty
import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 客户端管理视图对象 sys_client
 *
 * @author Lion Li
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ExcelIgnoreUnannotated
data class SysClientVo(
    /**
     * 客户端ID
     */
    @ExcelProperty(value = ["id"])
    var id: Long? = null,

    /**
     * 客户端ID
     */
    @ExcelProperty(value = ["客户端id"])
    var clientId: String? = null,

    /**
     * 客户端密钥
     */
    @ExcelProperty(value = ["客户端key"])
    var clientKey: String? = null,

    /**
     * 客户端名称
     */
    var clientName: String? = null,

    /**
     * 客户端类型
     */
    var clientType: String? = null,

    /**
     * 授权类型
     */
    @ExcelProperty(value = ["授权类型"])
    var grantType: String? = null,

    /**
     * 回调地址
     */
    var redirectUri: String? = null,

    /**
     * 授权自动登录
     */
    var autoApprove: Boolean? = null,

    /**
     * 状态（0正常 1停用）
     */
    @ExcelProperty(value = ["状态"])
    var status: String? = null,

    /**
     * 设备类型
     */
    var deviceType: String? = null,

    /**
     * Token 超时时间（秒）
     */
    @ExcelProperty(value = ["token固定超时时间"])
    var timeout: Long? = null,

    /**
     * Token 有效期（秒）
     */
    @ExcelProperty(value = ["token活跃超时时间"])
    var activeTimeout: Long? = null,

    /**
     * 备注
     */
    var remark: String? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null
)
