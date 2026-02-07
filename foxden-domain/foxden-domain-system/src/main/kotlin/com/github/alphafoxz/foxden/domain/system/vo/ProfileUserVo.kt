package com.github.alphafoxz.foxden.domain.system.vo

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 用户个人信息视图对象
 *
 * @author Lion Li
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class ProfileUserVo(
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
     * 头像地址
     */
    var avatar: Long? = null,

    /**
     * 部门名称
     */
    var deptName: String? = null
)
