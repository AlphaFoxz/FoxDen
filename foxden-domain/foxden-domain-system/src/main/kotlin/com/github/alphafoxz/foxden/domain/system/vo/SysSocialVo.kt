package com.github.alphafoxz.foxden.domain.system.vo

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 社会化关系视图对象 sys_social
 *
 * @author Lion Li
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class SysSocialVo(
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
    var type: String? = null,

    /**
     * 社交平台唯一标识
     */
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
     * 创建时间
     */
    var createTime: java.time.LocalDateTime? = null
)
