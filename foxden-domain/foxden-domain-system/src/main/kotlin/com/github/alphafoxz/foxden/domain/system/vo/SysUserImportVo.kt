package com.github.alphafoxz.foxden.domain.system.vo

import com.alibaba.excel.annotation.ExcelProperty
import com.github.alphafoxz.foxden.common.excel.annotation.ExcelDictFormat
import com.github.alphafoxz.foxden.common.excel.convert.ExcelDictConvert
import java.io.Serializable

/**
 * 用户对象导入VO
 *
 * @author Lion Li
 */
data class SysUserImportVo(
    /**
     * 用户ID
     */
    @field:ExcelProperty("用户序号")
    var userId: Long? = null,

    /**
     * 部门ID
     */
    @field:ExcelProperty("部门编号")
    var deptId: Long? = null,

    /**
     * 用户账号
     */
    @field:ExcelProperty("登录名称")
    var userName: String? = null,

    /**
     * 用户昵称
     */
    @field:ExcelProperty("用户名称")
    var nickName: String? = null,

    /**
     * 用户邮箱
     */
    @field:ExcelProperty("用户邮箱")
    var email: String? = null,

    /**
     * 手机号码
     */
    @field:ExcelProperty("手机号码")
    var phonenumber: String? = null,

    /**
     * 用户性别
     */
    @field:ExcelProperty(value = ["用户性别"], converter = ExcelDictConvert::class)
    @field:ExcelDictFormat(dictType = "sys_user_sex")
    var sex: String? = null,

    /**
     * 账号状态（0正常 1停用）
     */
    @field:ExcelProperty(value = ["账号状态"], converter = ExcelDictConvert::class)
    @field:ExcelDictFormat(dictType = "sys_normal_disable")
    var status: String? = null
) : Serializable
