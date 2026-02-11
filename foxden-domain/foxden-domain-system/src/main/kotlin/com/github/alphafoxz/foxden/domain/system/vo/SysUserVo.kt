package com.github.alphafoxz.foxden.domain.system.vo

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated
import com.alibaba.excel.annotation.ExcelProperty
import com.fasterxml.jackson.annotation.JsonInclude
import com.github.alphafoxz.foxden.common.sensitive.annotation.Sensitive
import com.github.alphafoxz.foxden.common.sensitive.core.SensitiveStrategy
import com.github.alphafoxz.foxden.common.translation.annotation.Translation
import com.github.alphafoxz.foxden.common.translation.constant.TransConstant

/**
 * 用户信息视图对象 sys_user
 *
 * @author Michelle.Chung
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ExcelIgnoreUnannotated
data class SysUserVo(
    /**
     * 用户ID
     */
    var userId: Long? = null,

    /**
     * 租户ID
     */
    var tenantId: String? = null,

    /**
     * 部门ID
     */
    var deptId: Long? = null,

    /**
     * 用户账号
     */
    var userName: String? = null,

    /**
     * 密码
     */
    var password: String? = null,

    /**
     * 用户昵称
     */
    var nickName: String? = null,

    /**
     * 用户类型（sys_user系统用户）
     */
    var userType: String? = null,

    /**
     * 用户邮箱
     */
    @Sensitive(strategy = SensitiveStrategy.EMAIL, perms = ["system:user:edit"])
    var email: String? = null,

    /**
     * 手机号码
     */
    @Sensitive(strategy = SensitiveStrategy.PHONE, perms = ["system:user:edit"])
    var phonenumber: String? = null,

    /**
     * 用户性别（0男 1女 2未知）
     */
    var sex: String? = null,

    /**
     * 帐号状态（0正常 1停用）
     */
    var status: String? = null,

    /**
     * 备注
     */
    var remark: String? = null,

    /**
     * 部门名称
     */
    var deptName: String? = null,

    /**
     * 角色组
     */
    var roles: List<SysRoleVo>? = null,

    /**
     * 角色ID组
     */
    var roleIds: List<Long>? = null,

    /**
     * 岗位组
     */
    var posts: List<SysPostVo>? = null,

    /**
     * 岗位ID组
     */
    var postIds: List<Long>? = null,

    /**
     * 角色ID
     */
    var roleId: Long? = null,

    /**
     * 最后登录IP
     */
    var loginIp: String? = null,

    /**
     * 头像
     */
    var avatar: Long? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null,

    /**
     * 登录日期
     */
    var loginDate: java.time.LocalDateTime? = null
)

/**
 * 用户导出视图对象
 */
@ExcelIgnoreUnannotated
data class SysUserExportVo(
    var userId: Long? = null,
    var deptId: Long? = null,
    @ExcelProperty(value = ["用户账号"])
    var userName: String? = null,
    @ExcelProperty(value = ["用户昵称"])
    var nickName: String? = null,
    var userType: String? = null,
    var email: String? = null,
    var phonenumber: String? = null,
    var sex: String? = null,
    var status: String? = null,
    @ExcelProperty(value = ["部门名称"])
    var deptName: String? = null,
    @ExcelProperty(value = ["负责人"])
    var leader: String? = null
)
