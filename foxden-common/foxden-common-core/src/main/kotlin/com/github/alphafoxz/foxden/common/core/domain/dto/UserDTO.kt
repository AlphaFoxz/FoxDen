package com.github.alphafoxz.foxden.common.core.domain.dto

import java.io.Serializable
import java.util.Date

/**
 * 用户
 */
data class UserDTO(
    /**
     * 用户ID
     */
    var userId: Long? = null,

    /**
     * 部门ID
     */
    var deptId: Long? = null,

    /**
     * 用户账号
     */
    var userName: String? = null,

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
     * 帐号状态（0正常 1停用）
     */
    var status: String? = null,

    /**
     * 创建时间
     */
    var createTime: Date? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
