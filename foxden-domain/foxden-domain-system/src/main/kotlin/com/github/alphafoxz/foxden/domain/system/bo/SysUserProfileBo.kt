package com.github.alphafoxz.foxden.domain.system.bo

import jakarta.validation.constraints.Size

/**
 * 用户个人信息业务对象
 *
 * @author Lion Li
 */
data class SysUserProfileBo(
    /**
     * 用户ID
     */
    var userId: Long? = null,

    /**
     * 用户昵称
     */
    @get:Size(min = 0, max = 30, message = "用户昵称长度不能超过{max}个字符")
    var nickName: String? = null,

    /**
     * 用户邮箱
     */
    @get:Size(min = 0, max = 50, message = "邮箱长度不能超过{max}个字符")
    var email: String? = null,

    /**
     * 手机号码
     */
    @get:Size(min = 0, max = 11, message = "手机号码长度不能超过{max}个字符")
    var phonenumber: String? = null,

    /**
     * 用户性别（0男 1女 2未知）
     */
    var sex: String? = null
)
