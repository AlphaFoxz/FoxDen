package com.github.alphafoxz.foxden.domain.system.bo

import com.github.alphafoxz.foxden.common.core.validate.AddGroup
import com.github.alphafoxz.foxden.common.core.validate.EditGroup
import com.github.alphafoxz.foxden.common.core.xss.Xss
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * 用户信息业务对象 sys_user
 *
 * @author Michelle.Chung
 */
data class SysUserBo(
    /**
     * 用户ID
     */
    var userId: Long? = null,

    /**
     * 部门ID
     */
    var deptId: Long? = null,

    /**
     * 角色ID（用于查询已分配/未分配用户列表）
     */
    var roleId: Long? = null,

    /**
     * 用户账号
     */
    @field:Xss(message = "用户账号不能包含脚本字符")
    @get:NotBlank(message = "用户账号不能为空", groups = [AddGroup::class, EditGroup::class])
    @get:Size(min = 0, max = 30, message = "用户账号长度不能超过{max}个字符")
    var userName: String? = null,

    /**
     * 用户昵称
     */
    @field:Xss(message = "用户昵称不能包含脚本字符")
    @get:NotBlank(message = "用户昵称不能为空", groups = [AddGroup::class, EditGroup::class])
    @get:Size(min = 0, max = 30, message = "用户昵称长度不能超过{max}个字符")
    var nickName: String? = null,

    /**
     * 用户类型（sys_user系统用户）
     */
    var userType: String? = null,

    /**
     * 用户邮箱
     */
    @get:Email(message = "邮箱格式不正确")
    @get:Size(min = 0, max = 50, message = "邮箱长度不能超过{max}个字符")
    var email: String? = null,

    /**
     * 手机号码
     */
    var phonenumber: String? = null,

    /**
     * 用户性别（0男 1女 2未知）
     */
    var sex: String? = null,

    /**
     * 用户ID列表（逗号分隔，用于批量查询）
     */
    var userIds: String? = null,

    /**
     * 排除的用户ID（逗号分隔）
     */
    var excludeUserIds: String? = null,

    /**
     * 开始时间
     */
    var beginTime: java.time.LocalDateTime? = null,

    /**
     * 结束时间
     */
    var endTime: java.time.LocalDateTime? = null,

    /**
     * 密码
     */
    var password: String? = null,

    /**
     * 帐号状态（0正常 1停用）
     */
    var status: String? = null,

    /**
     * 最后登录IP
     */
    var loginIp: String? = null,

    /**
     * 最后登录时间
     */
    var loginDate: java.time.LocalDateTime? = null,

    /**
     * 备注
     */
    var remark: String? = null,

    /**
     * 角色组
     */
    @get:Size(min = 1, message = "用户角色不能为空")
    var roleIds: List<Long>? = null,

    /**
     * 岗位组
     */
    var postIds: List<Long>? = null,

    /**
     * 创建者
     */
    var createBy: String? = null,

    /**
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null,

    /**
     * 更新者
     */
    var updateBy: String? = null,

    /**
     * 更新时间
     */
    var updateTime: java.time.LocalDateTime? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SysUserBo) return false
        return userId == other.userId
    }

    override fun hashCode(): Int {
        return userId?.hashCode() ?: 0
    }
}
