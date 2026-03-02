package com.github.alphafoxz.foxden.domain.system.vo

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated
import com.alibaba.excel.annotation.ExcelProperty
import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 租户视图对象 sys_tenant
 *
 * @author Lion Li
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ExcelIgnoreUnannotated
data class SysTenantVo(
    /**
     * id
     */
    @ExcelProperty(value = ["id"])
    var id: Long? = null,

    /**
     * 租户ID
     */
    @ExcelProperty(value = ["租户编号"])
    var tenantId: String? = null,

    /**
     * 联系人
     */
    @ExcelProperty(value = ["联系人"])
    var contactUserName: String? = null,

    /**
     * 联系电话
     */
    @ExcelProperty(value = ["联系电话"])
    var contactPhone: String? = null,

    /**
     * 企业名称
     */
    @ExcelProperty(value = ["企业名称"])
    var companyName: String? = null,

    /**
     * 用户名（租户管理员账号）
     */
    var username: String? = null,

    /**
     * 用户昵称
     */
    var nickName: String? = null,

    /**
     * 用户邮箱
     */
    var email: String? = null,

    /**
     * 统一社会信用代码
     */
    @ExcelProperty(value = ["统一社会信用代码"])
    var licenseNumber: String? = null,

    /**
     * 地址
     */
    @ExcelProperty(value = ["地址"])
    var address: String? = null,

    /**
     * 域名
     */
    @ExcelProperty(value = ["域名"])
    var domain: String? = null,

    /**
     * 企业简介
     */
    @ExcelProperty(value = ["企业简介"])
    var intro: String? = null,

    /**
     * 备注
     */
    @ExcelProperty(value = ["备注"])
    var remark: String? = null,

    /**
     * 租户套餐ID
     */
    @ExcelProperty(value = ["租户套餐编号"])
    var packageId: Long? = null,

    /**
     * 租户套餐名称
     */
    var packageName: String? = null,

    /**
     * 过期时间
     */
    @ExcelProperty(value = ["过期时间"])
    var expireTime: java.time.LocalDateTime? = null,

    /**
     * 用户数量
     */
    @ExcelProperty(value = ["用户数量"])
    var accountCount: Long? = null,

    /**
     * 租户状态（0正常 1停用）
     */
    @ExcelProperty(value = ["租户状态"])
    var status: String? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null
)
