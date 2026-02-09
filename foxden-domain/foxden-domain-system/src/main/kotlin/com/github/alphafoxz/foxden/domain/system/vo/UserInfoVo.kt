package com.github.alphafoxz.foxden.domain.system.vo

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * 登录用户信息
 *
 * @author FoxDen Team
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class UserInfoVo(
    /**
     * 用户基本信息
     */
    var user: SysUserVo? = null,

    /**
     * 菜单权限
     */
    var permissions: Set<String>? = null,

    /**
     * 角色权限
     */
    var roles: Set<String>? = null
)
