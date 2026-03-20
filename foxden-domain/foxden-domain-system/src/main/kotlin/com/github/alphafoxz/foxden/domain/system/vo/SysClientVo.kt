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
     * 客户端id
     */
    @ExcelProperty(value = ["客户端id"])
    var clientId: String? = null,

    /**
     * 客户端key
     */
    @ExcelProperty(value = ["客户端key"])
    var clientKey: String? = null,

    /**
     * 客户端秘钥
     */
    @ExcelProperty(value = ["客户端秘钥"])
    var clientSecret: String? = null,

    /**
     * 授权类型（列表格式，用于导出Excel）
     */
    @ExcelProperty(value = ["授权类型"], converter = com.github.alphafoxz.foxden.common.excel.convert.ExcelListConvert::class)
    var grantTypeList: List<String>? = null,

    /**
     * 授权类型（字符串格式，用于存储，逗号分隔）
     */
    var grantType: String? = null,

    /**
     * 设备类型
     */
    var deviceType: String? = null,

    /**
     * token活跃超时时间（秒）
     */
    @ExcelProperty(value = ["token活跃超时时间"])
    var activeTimeout: Long? = null,

    /**
     * token固定超时时间（秒）
     */
    @ExcelProperty(value = ["token固定超时时间"])
    var timeout: Long? = null,

    /**
     * 状态（0正常 1停用）
     */
    @ExcelProperty(value = ["状态"])
    var status: String? = null
)
