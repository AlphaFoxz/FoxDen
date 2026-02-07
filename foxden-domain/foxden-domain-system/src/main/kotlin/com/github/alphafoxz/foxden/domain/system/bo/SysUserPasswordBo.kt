package com.github.alphafoxz.foxden.domain.system.bo

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * 用户密码业务对象
 *
 * @author Lion Li
 */
data class SysUserPasswordBo(
    /**
     * 用户ID
     */
    var userId: Long? = null,

    /**
     * 旧密码
     */
    @get:NotBlank(message = "旧密码不能为空")
    @get:Size(min = 0, max = 30, message = "旧密码长度不能超过{max}个字符")
    var oldPassword: String? = null,

    /**
     * 新密码
     */
    @get:NotBlank(message = "新密码不能为空")
    @get:Size(min = 0, max = 30, message = "新密码长度不能超过{max}个字符")
    var newPassword: String? = null
)
