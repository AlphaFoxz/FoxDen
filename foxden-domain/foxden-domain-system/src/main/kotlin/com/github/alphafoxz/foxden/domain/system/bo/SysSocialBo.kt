package com.github.alphafoxz.foxden.domain.system.bo

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * 社会化关系业务对象 sys_social
 *
 * @author Lion Li
 */
data class SysSocialBo(
    /**
     * 主键ID
     */
    var socialId: Long? = null,

    /**
     * 租户ID
     */
    var tenantId: String? = null,

    /**
     * 用户ID
     */
    var userId: Long? = null,

    /**
     * 社交平台类型
     */
    @get:NotBlank(message = "社交平台类型不能为空")
    var type: String? = null,

    /**
     * 社交平台唯一标识
     */
    @get:NotBlank(message = "社交平台唯一标识不能为空")
    @get:Size(min = 0, max = 255, message = "社交平台唯一标识长度不能超过{max}个字符")
    var openid: String? = null,

    /**
     * 访问令牌
     */
    var accessToken: String? = null,

    /**
     * 过期时间
     */
    var expireIn: Int? = null,

    /**
     * 刷新令牌
     */
    var refreshToken: String? = null,

    /**
     * 备注
     */
    var remark: String? = null
)
